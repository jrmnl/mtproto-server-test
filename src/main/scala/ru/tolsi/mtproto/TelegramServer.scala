package ru.tolsi.mtproto

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Tcp.{IncomingConnection, ServerBinding}
import akka.stream.scaladsl.{Flow, Framing, Source, Tcp}
import akka.util.ByteString
import com.typesafe.scalalogging.StrictLogging
import ru.tolsi.mtproto.messages.UnencryptedMessage
import ru.tolsi.mtproto.messages.serialization.codecs
import scodec.bits.BitVector

import scala.concurrent.Future

object TelegramServer extends StrictLogging {
  val lengthField = 4
  private[mtproto] def telegramServerFlow(): Flow[ByteString, ByteString, NotUsed] = Flow[ByteString]
    .via(Framing.lengthField(lengthField, 0, 1000))
    .map(m => codecs.unencryptedMessageCodec.decode(BitVector(m.drop(lengthField))).require.value)
    .via(new AuthProcess())
    .map(m => ByteString(codecs.unencryptedMessageCodec.encode(UnencryptedMessage(0, 0, m)).require.bytes.toArray))

  def main(args: Array[String]): Unit = {
    implicit val as: ActorSystem = ActorSystem()
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    val connections: Source[IncomingConnection, Future[ServerBinding]] =
      Tcp().bind("localhost", 3000)

    logger.info("Demo telegram server listen you on 'localhost:3000'")

    connections runForeach { connection ⇒
      logger.info(s"New connection from: ${connection.remoteAddress}")
      connection.handleWith(telegramServerFlow())
    }
  }
}

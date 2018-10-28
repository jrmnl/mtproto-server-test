package ru.tolsi.mtproto

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Tcp.{IncomingConnection, ServerBinding}
import akka.stream.scaladsl.{Source, Tcp}
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.Future

object Server extends App with StrictLogging {
  implicit val as: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val connections: Source[IncomingConnection, Future[ServerBinding]] =
    Tcp().bind("localhost", 3000)

  connections runForeach { connection ⇒
    logger.info(s"New connection from: ${connection.remoteAddress}")

//    val echo = Flow[ByteString]
//      .via(Framing.delimiter(
//        ByteString("\n"),
//        maximumFrameLength = 256,
//        allowTruncation = true))
//      .map()
//      .via(new AuthProcess())
//      .map(ByteString(_))
//
//    connection.handleWith(echo)
  }
}

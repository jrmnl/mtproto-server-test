package ru.tolsi.mtproto

import java.nio.ByteBuffer

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep}
import akka.stream.testkit.{TestPublisher, TestSubscriber}
import akka.stream.testkit.scaladsl.{TestSink, TestSource}
import akka.util.ByteString
import org.scalatest.{Matchers, WordSpec}
import ru.tolsi.mtproto.messages._
import ru.tolsi.mtproto.messages.serialization.codecs
import ru.tolsi.mtproto.util.{ByteString => BS, _}
import scodec.bits.BitVector

import scala.concurrent.duration._

class TelegramServerSpec extends WordSpec with Matchers  {
  implicit val as: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  def testPubSub(flow: Flow[ByteString, ByteString, NotUsed]): (TestPublisher.Probe[ByteString], TestSubscriber.Probe[ByteString]) = TestSource.probe[ByteString]
    .via(flow)
    .toMat(TestSink.probe[ByteString])(Keep.both)
    .run()

  "Telegram server" should {
    "close connection after a wrong auth message" in {
      val flow = TelegramServer.telegramServerFlow()
      val (pub, sub) = testPubSub(flow)

      sub.request(1)
      pub.sendNext(ByteString.fromArray(codecs.unencryptedMessageCodec.encode(UnencryptedMessage(0,0,ResPq.createRandom)).require.toByteArray))
      sub.expectError()
    }

    "close connection after a valid auth messages sequence" in {
      val flow = TelegramServer.telegramServerFlow()

      val (pub, sub) = testPubSub(flow)

      sub.request(1)
      val nonce = createRandomBytes(16)
      val reqPqMessage = UnencryptedMessage(0,0,ReqPq(BS(nonce)))
      val reqPqMessageBytes = codecs.unencryptedMessageCodec.encode(reqPqMessage).require.toByteArray
      pub.sendNext(ByteString.fromArray(reqPqMessageBytes))
      val response = sub.expectNext(5.seconds)
      val answer = codecs.unencryptedMessageCodec.decode(BitVector(response.toArray[Byte])).require.value
      answer.message.asInstanceOf[ResPq].nonce should be(BS(nonce))

      sub.request(1)
      val serverNonce = createRandomBytes(16)
      val reqDHParamsMessage = UnencryptedMessage(0,0,ReqDHParams(BS(nonce), BS(serverNonce), BigInt("1599411841"), BigInt("1913360507"), 0xc3b42b026ce86b21L,
        EncyptedPqInnerData(BS.fromHex("442502BD09AFD51C2C34459A20319CEBDCE4D399B103DC96191BB078F7EACCDF63457472D8260EE81D31DFE4A9B83ED79D8851D39D109E5163C0F7F73316CB43E9B5FA152F4D62BB3D14C8AAEA39C6816DAB16AB521657808CABE762A0B5D77EEBA0F53C9FFB7396F24503FB29B16D14AD5764A9D4B24CC6BC1B0C5347BF4245238AD5F9CDA85220A98B0EE106BEE50D7E7E055C3B84576D37F37580E4C272DABE099008E1FFD89BD713A7F4E23DD0658B1B103A73C22D0B93CB96568117A9C8D5C8C5D73EA53997C39BA0A3C2C8E46C2B6C7485E0B24290E25EE3181B443D7A4F9B3D9594277B94E10D26E8D4F6807561274A3D18971A4D6ADB86FF0A10547A"))))
      val reqDHParamsMessageBytes = codecs.unencryptedMessageCodec.encode(reqDHParamsMessage).require.toByteArray
      pub.sendNext(ByteString.fromArray(reqDHParamsMessageBytes))
      sub.expectComplete()
    }
  }
}

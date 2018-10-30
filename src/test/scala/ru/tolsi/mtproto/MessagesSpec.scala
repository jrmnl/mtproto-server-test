package ru.tolsi.mtproto

import org.scalatest.{Matchers, WordSpec}
import ru.tolsi.mtproto.crypto.rsa.RSAKeyPair
import ru.tolsi.mtproto.messages.serialization.codecs
import ru.tolsi.mtproto.messages._
import ru.tolsi.mtproto.util._
import scodec.bits._

class MessagesSpec extends WordSpec with Matchers {

  private val p = BigInt("1599411841")
  private val q = BigInt("1913360507")
  private val pq = p * q
  private val nonce = ByteString(hex"3e0549828cca27e966b301a48fece2fc".toArray)
  private val serverNonce = ByteString(hex"dc714e5c6937631e37ce313f3b95ab0e".toArray)
  private val newNonce = ByteString(hex"406073d8d586a504a73dd63ea2357703b2e258bec4216c51b56554fc634afea2".toArray)

  private val pqInnerData = PqInnerData(pq = pq, p = p, q = q, nonce = nonce, serverNonce = serverNonce, newNonce = newNonce)

  "ReqPq codec" should {
    "encode a valid message" in {
      val blob = hex"789746603e0549828cca27e966b301a48fece2fc".bits
      val message = ReqPq(nonce)
      codecs.reqPqCodec.encode(message).require should be(blob)
    }

    "decode a valid message" in {
      val blob = hex"789746603e0549828cca27e966b301a48fece2fc".bits
      val message = ReqPq(nonce)
      codecs.reqPqCodec.decode(blob).require.value should be(message)
    }
  }

  "UnencryptedMessage codec" should {
    "encode a valid message" in {
      val blob = hex"00000000000000004a967027c47ae55140000000632416053e0549828cca27e966b301a48fece2fcdc714e5c6937631e37ce313f3b95ab0e082a78327b14155ffb00000015c4b51c01000000216be86c022bb4c3".bits
      val message = UnencryptedMessage(0, 0x51e57ac42770964aL, ResPq(nonce, serverNonce, pq, TlVector(List(0xc3b42b026ce86b21L))))
      codecs.unencryptedMessageCodec.encode(message).require should be(blob)
    }

    "decode a valid message" in {
      val blob = hex"000000000000000001c8831ec97ae55114000000789746603e0549828cca27e966b301a48fece2fc".bits
      val message = UnencryptedMessage(0, 0x51E57AC91E83C801L, ReqPq(nonce))
      codecs.unencryptedMessageCodec.decode(blob).require.value should be(message)
    }
  }

  "ResPq codec" should {
    "encode a valid message" in {
      val blob = hex"632416053e0549828cca27e966b301a48fece2fcdc714e5c6937631e37ce313f3b95ab0e082a78327b14155ffb00000015c4b51c01000000216be86c022bb4c3".bits
      val message = ResPq(nonce, serverNonce, pq, TlVector(List(0xc3b42b026ce86b21L)))
      codecs.resPqCodec.encode(message).require should be(blob)
    }

    "decode a valid message" in {
      val blob = hex"632416053e0549828cca27e966b301a48fece2fcdc714e5c6937631e37ce313f3b95ab0e082a78327b14155ffb00000015c4b51c01000000216be86c022bb4c3".bits
      val message = ResPq(nonce, serverNonce, pq, TlVector(List(0xc3b42b026ce86b21L)))
      codecs.resPqCodec.decode(blob).require.value should be(message)
    }
  }

  "PqInnerData codec" should {
    "encode a valid message" in {
      val blob =
        hex"ec5ac983082a78327b14155ffb000000045f55168100000004720b907b0000003e0549828cca27e966b301a48fece2fcdc714e5c6937631e37ce313f3b95ab0e406073d8d586a504a73dd63ea2357703b2e258bec4216c51b56554fc634afea2".toArray
      codecs.pqInnerDataCodec.encode(pqInnerData).require.toByteArray should be(blob)
    }

    "decode a valid message" in {
      val blob =
        hex"ec5ac983082a78327b14155ffb000000045f55168100000004720b907b0000003e0549828cca27e966b301a48fece2fcdc714e5c6937631e37ce313f3b95ab0e406073d8d586a504a73dd63ea2357703b2e258bec4216c51b56554fc634afea2".bits
      codecs.pqInnerDataCodec.decode(blob).require.value should be(pqInnerData)
    }
  }

  "PqInnerData" should {
    "RSA encoding roundtrip" in {
      val rsaKeys = RSAKeyPair.generate()
      val encrypted = EncyptedPqInnerData.encrypt(pqInnerData, rsaKeys.publicKey)
      EncyptedPqInnerData.decrypt(encrypted.encrypted.arr, rsaKeys.privateKey).require.value should be(pqInnerData)
    }
  }

  "ReqDHParams codec" should {
    "encode a valid message" in {
      val pqInnerData = PqInnerData(pq = pq, p = p, q = q, nonce = nonce, serverNonce = serverNonce, newNonce = newNonce)

      val req = ReqDHParams(nonce, serverNonce, p, q, 0xc3b42b026ce86b21L, EncyptedPqInnerData(ByteString.fromHex("442502BD09AFD51C2C34459A20319CEBDCE4D399B103DC96191BB078F7EACCDF63457472D8260EE81D31DFE4A9B83ED79D8851D39D109E5163C0F7F73316CB43E9B5FA152F4D62BB3D14C8AAEA39C6816DAB16AB521657808CABE762A0B5D77EEBA0F53C9FFB7396F24503FB29B16D14AD5764A9D4B24CC6BC1B0C5347BF4245238AD5F9CDA85220A98B0EE106BEE50D7E7E055C3B84576D37F37580E4C272DABE099008E1FFD89BD713A7F4E23DD0658B1B103A73C22D0B93CB96568117A9C8D5C8C5D73EA53997C39BA0A3C2C8E46C2B6C7485E0B24290E25EE3181B443D7A4F9B3D9594277B94E10D26E8D4F6807561274A3D18971A4D6ADB86FF0A10547A")))
      val blob =
        hex"bee412d73e0549828cca27e966b301a48fece2fcdc714e5c6937631e37ce313f3b95ab0e045f55168100000004720b907b000000216be86c022bb4c3fe000100442502bd09afd51c2c34459a20319cebdce4d399b103dc96191bb078f7eaccdf63457472d8260ee81d31dfe4a9b83ed79d8851d39d109e5163c0f7f73316cb43e9b5fa152f4d62bb3d14c8aaea39c6816dab16ab521657808cabe762a0b5d77eeba0f53c9ffb7396f24503fb29b16d14ad5764a9d4b24cc6bc1b0c5347bf4245238ad5f9cda85220a98b0ee106bee50d7e7e055c3b84576d37f37580e4c272dabe099008e1ffd89bd713a7f4e23dd0658b1b103a73c22d0b93cb96568117a9c8d5c8c5d73ea53997c39ba0a3c2c8e46c2b6c7485e0b24290e25ee3181b443d7a4f9b3d9594277b94e10d26e8d4f6807561274a3d18971a4d6adb86ff0a10547a".bits
      val b = codecs.reqDHParamsCodec.encode(req).require.toByteArray
      b should be(blob.toByteArray)
    }

    "decode a valid message" in {
      val req = ReqDHParams(nonce, serverNonce, p, q, 0xc3b42b026ce86b21L, EncyptedPqInnerData(ByteString.fromHex("442502BD09AFD51C2C34459A20319CEBDCE4D399B103DC96191BB078F7EACCDF63457472D8260EE81D31DFE4A9B83ED79D8851D39D109E5163C0F7F73316CB43E9B5FA152F4D62BB3D14C8AAEA39C6816DAB16AB521657808CABE762A0B5D77EEBA0F53C9FFB7396F24503FB29B16D14AD5764A9D4B24CC6BC1B0C5347BF4245238AD5F9CDA85220A98B0EE106BEE50D7E7E055C3B84576D37F37580E4C272DABE099008E1FFD89BD713A7F4E23DD0658B1B103A73C22D0B93CB96568117A9C8D5C8C5D73EA53997C39BA0A3C2C8E46C2B6C7485E0B24290E25EE3181B443D7A4F9B3D9594277B94E10D26E8D4F6807561274A3D18971A4D6ADB86FF0A10547A")))
      val blob =
        hex"bee412d73e0549828cca27e966b301a48fece2fcdc714e5c6937631e37ce313f3b95ab0e045f55168100000004720b907b000000216be86c022bb4c3fe000100442502bd09afd51c2c34459a20319cebdce4d399b103dc96191bb078f7eaccdf63457472d8260ee81d31dfe4a9b83ed79d8851d39d109e5163c0f7f73316cb43e9b5fa152f4d62bb3d14c8aaea39c6816dab16ab521657808cabe762a0b5d77eeba0f53c9ffb7396f24503fb29b16d14ad5764a9d4b24cc6bc1b0c5347bf4245238ad5f9cda85220a98b0ee106bee50d7e7e055c3b84576d37f37580e4c272dabe099008e1ffd89bd713a7f4e23dd0658b1b103a73c22d0b93cb96568117a9c8d5c8c5d73ea53997c39ba0a3c2c8e46c2b6c7485e0b24290e25ee3181b443d7a4f9b3d9594277b94e10d26e8d4f6807561274a3d18971a4d6adb86ff0a10547a".bits
      codecs.reqDHParamsCodec.decode(blob).require.value should be(req)
    }
  }

}

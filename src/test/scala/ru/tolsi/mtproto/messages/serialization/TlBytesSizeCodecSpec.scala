package ru.tolsi.mtproto.messages.serialization

import org.scalatest.{Matchers, WordSpec}
import ru.tolsi.mtproto.util.ByteString
import scodec.bits.BitVector

class TlBytesSizeCodecSpec extends WordSpec with Matchers {
  val codec = TlBytesSizeCodec

  "Telegram size codec" should {
    "serialize small number (<254)" in {
      val value = 250
      val expected = Array(value.toByte)
      val encoded = codec.encode(value).require.toByteArray
      ByteString(encoded) should be(ByteString(expected))
    }

    "serialize big number (>=254)" in {
      val value = 261
      val expected = Array[Byte](-2, 5, 1, 0)
      val encoded = codec.encode(value).require.toByteArray
      ByteString(encoded) should be(ByteString(expected))
    }

    "deserialize small number (<254)" in {
      val expected = 250
      val array = Array(expected.toByte)

      val encoded = codec.decode(BitVector(array)).require.value
      encoded should be(expected)
    }

    "deserialize big number (>=254)" in {
      val expected = 1000
      val array = Array[Byte](-2, -24, 3, 0)

      val encoded = codec.decode(BitVector(array)).require.value
      encoded should be(expected)
    }
  }
}

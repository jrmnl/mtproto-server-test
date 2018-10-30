package ru.tolsi.mtproto.messages.serialization

import org.scalatest.{Matchers, WordSpec}
import ru.tolsi.mtproto.util.ByteString
import scodec.bits.{BitVector, ByteVector}
import scodec.codecs.bytes

class TlBytesCodecSpec extends WordSpec with Matchers {

  val codec = new TlBytesCodec(bytes)

  "Telegram bytes codec" should {
    "serialize small bytes array without alignment" in {
      val array = Array[Byte](1, 4, 34, -23, 23, 66, 9)
      val expected = 7.toByte +: array
      val encoded = codec.encode(ByteVector(array)).require.toByteArray
      ByteString(encoded) should be(ByteString(expected))
    }

    "serialize small bytes array with alignment" in {
      val array = "asdfaslkdjfalsjkdfhlkasjdf".getBytes
      val expected = 26.toByte +: array :+ 0.toByte
      val encoded = codec.encode(ByteVector(array)).require.toByteArray
      ByteString(encoded) should be(ByteString(expected))
    }

    "serialize big bytes array without alignment" in {
      val array = ("a" * 260).getBytes
      val expected = Array[Byte](-2, 4, 1, 0) ++ array
      val encoded = codec.encode(ByteVector(array)).require.toByteArray
      ByteString(encoded) should be(ByteString(expected))
    }

    "serialize big bytes array with alignment" in {
      val array = ("a" * 261).getBytes
      val expected = Array[Byte](-2, 5, 1, 0) ++ array ++ Array[Byte](0, 0, 0)
      val encoded = codec.encode(ByteVector(array)).require.toByteArray
      ByteString(encoded) should be(ByteString(expected))
    }

    "deserialize small bytes array without alignment" in {
      val expected = Array[Byte](1, 4, 34, -23, 23, 66, 9)
      val array = 7.toByte +: expected
      val decoded = codec.decode(BitVector(array)).require.value
      ByteString(decoded.toArray) should be(ByteString(expected))
    }

    "deserialize small bytes array with alignment" in {
      val expected = "asdfaslkdjfalsjkdfhlkasjdf".getBytes
      val array = 26.toByte +: expected :+ 0.toByte
      val decoded = codec.decode(BitVector(array)).require.value
      ByteString(decoded.toArray) should be(ByteString(expected))
    }

    "deserialize big bytes array without alignment" in {
      val expected = ("a" * 260).getBytes
      val array = Array[Byte](-2, 4, 1, 0) ++ expected
      val decoded = codec.decode(BitVector(array)).require.value
      ByteString(decoded.toArray) should be(ByteString(expected))
    }

    "deserialize big bytes array with alignment" in {
      val expected = ("a" * 261).getBytes
      val array = Array[Byte](-2, 5, 1, 0) ++ expected ++ Array[Byte](0, 0, 0)
      val decoded = codec.decode(BitVector(array)).require.value
      ByteString(decoded.toArray) should be(ByteString(expected))
    }
  }
}

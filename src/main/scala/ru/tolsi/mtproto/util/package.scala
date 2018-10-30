package ru.tolsi.mtproto

import scodec.Codec
import scodec.bits.ByteVector

import scala.util.Random

package object util {
  implicit class RichByteArray(val self: Array[Byte]) extends AnyVal {
    def toHex: String = self.map("%02X" format _).mkString
  }

  implicit class RichCodecByteVector(val codec: Codec[ByteVector]) extends AnyVal {
    def asByteString: Codec[ByteString] = codec.xmap[ByteString](bv => ByteString(bv.toArray), bs => ByteVector(bs.arr))
  }

  private val r = new Random()
  def createRandomBigInt(n: Int): BigInt = BigInt(createRandomBytes(n))
  def createRandomString(n: Int): String = r.alphanumeric.take(n).mkString
  def createRandomBytes(n: Int): Array[Byte] = {
    val a = new Array[Byte](n)
    r.nextBytes(a)
    a
  }
}

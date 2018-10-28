package ru.tolsi

import java.math.BigInteger

import scodec.Codec
import scodec.bits.ByteVector
import scodec.codecs._
import scodec.bits._
import shapeless.HNil

import scala.language.implicitConversions
import scala.util.Random

package object mtproto {
  private val r = new Random()

  def createRandomString(n: Int): String = r.alphanumeric.take(n).mkString

  def createRandomBytes(n: Int): Array[Byte] = {
    val a = new Array[Byte](n)
    r.nextBytes(a)
    a
  }

  implicit class RichByteArray(val self: Array[Byte]) extends AnyVal {
    def toHex: String = self.map("%02X" format _).mkString
  }

  implicit class RichCodecByteVector(val codec: Codec[ByteVector]) extends AnyVal {
    def asByteString: Codec[ByteString] = codec.xmap[ByteString](bv => ByteString(bv.toArray), bs => ByteVector(bs.arr))
  }

  def bytesString(n: Int): Codec[ByteString] = bytes(n).asByteString
  def bytesString: Codec[ByteString] = bytes.asByteString
  def tlBytesString: Codec[ByteString] = variableSizeBytes(int32, bytes).asByteString

  def fromBigInt(v: BigInteger): Array[Byte] = {
    val array = v.toByteArray
    if (array(0) == 0) {
      val res2 = new Array[Byte](array.length - 1)
      System.arraycopy(array, 1, res2, 0, res2.length)
      res2
    } else array
  }

  def tlBytesAsBigInt: Codec[BigInt] = (variableSizeBytes(int8, bytes) :: constant(BitVector(Array.fill[Byte](3)(0)))).dropUnits.
    xmap(h => BigInt(1, h.head.toArray), bi => ByteVector(fromBigInt(bi.bigInteger)) :: HNil)
}

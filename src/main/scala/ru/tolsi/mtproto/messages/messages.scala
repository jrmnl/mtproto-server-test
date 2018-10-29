package ru.tolsi.mtproto

import java.math.BigInteger

import ru.tolsi.mtproto.util.{ByteString, _}
import scodec._
import scodec.bits.{BitVector, ByteVector}
import scodec.codecs._
import shapeless.HNil

package object messages {
  trait MTProtoRequestMessage

  trait MTProtoResponceMessage

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

  // todo merge with prev by size
  // todo fix align
  def tlBytesStringAsBigInt: Codec[BigInt] = variableSizeBytes(int32L, bytes).
    xmap(h => BigInt(1, h.toArray), bi => ByteVector(fromBigInt(bi.bigInteger)))
}
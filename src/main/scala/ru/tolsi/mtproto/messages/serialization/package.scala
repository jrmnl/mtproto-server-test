package ru.tolsi.mtproto.messages

import java.math.BigInteger

import ru.tolsi.mtproto.util.ByteString
import scodec.Codec
import scodec.bits.ByteVector
import scodec.codecs.bytes

import ru.tolsi.mtproto.util._


package object serialization {

  def bytesString(n: Int): Codec[ByteString] = bytes(n).asByteString

  def bytesString: Codec[ByteString] = bytes.asByteString

  def fromBigInt(v: BigInteger): Array[Byte] = {
    val array = v.toByteArray
    if (array(0) == 0) {
      val res2 = new Array[Byte](array.length - 1)
      System.arraycopy(array, 1, res2, 0, res2.length)
      res2
    } else array
  }

  def tlBytesCodec[A](value: Codec[A]) = new TlBytesCodec(value)

  def tlBytesString: Codec[ByteString] = tlBytesCodec(bytes).asByteString

  def tlBytesAsBigInt: Codec[BigInt] = tlBytesCodec(bytes).
    xmap(h => BigInt(1, h.toArray), bi => ByteVector(fromBigInt(bi.bigInteger)))
}

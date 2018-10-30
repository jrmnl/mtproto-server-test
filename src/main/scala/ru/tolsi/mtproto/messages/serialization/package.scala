package ru.tolsi.mtproto.messages

import ru.tolsi.mtproto.util.ByteString
import scodec.Codec
import scodec.bits.ByteVector
import scodec.codecs.bytes

import ru.tolsi.mtproto.util._


package object serialization {

  def bytesString(n: Int): Codec[ByteString] = bytes(n).asByteString

  def bytesString: Codec[ByteString] = bytes.asByteString

  def tlBytesCodec[A](value: Codec[A]) = new TlBytesCodec(value)

  def tlBytesString: Codec[ByteString] = tlBytesCodec(bytes).asByteString

  def tlBytesAsBigInt: Codec[BigInt] = tlBytesCodec(bytes).xmap(h => BigInt(h.toArray), bi => ByteVector(bi.bigInteger.toByteArray))
}

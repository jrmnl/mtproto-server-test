package ru.tolsi.mtproto.messages.serialization

import scodec.{Attempt, Codec, DecodeResult, SizeBound}
import scodec.bits.BitVector
import scodec.codecs.{byte, fixedSizeBits}

final class TlBytesCodec[A](valueCodec: Codec[A]) extends Codec[A] {
  private val decoder = TlBytesSizeCodec flatMap { size => fixedSizeBits(size * 8, valueCodec) }

  def sizeBound: SizeBound = byte.sizeBound.atLeast

  override def encode(a: A): Attempt[BitVector] = for {
    encA <- valueCodec.encode(a)
    encSize <- TlBytesSizeCodec.encode(encA.bytes.size.intValue())
    alignment = {
      val mod = ((encA.bytes.size + encSize.bytes.size) % 4).intValue()
      if (mod != 0) {
        BitVector(Array.fill(4 - mod)(0.toByte))
      } else {
        BitVector.empty
      }
    }
  } yield encSize ++ encA ++ alignment

  override def decode(buffer: BitVector): Attempt[DecodeResult[A]] = decoder.decode(buffer)

  override def toString = s"tgBytesCodec($valueCodec)"
}
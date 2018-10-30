package ru.tolsi.mtproto.messages.serialization

import scodec.{Attempt, Codec, DecodeResult, SizeBound}
import scodec.bits.BitVector
import scodec.codecs._

final class TlBytesCodec[A](valueCodec: Codec[A]) extends Codec[A] {
  private val decoder = TlBytesSizeCodec flatMap { size =>
    val extra = if (size >= 254) 4 else 1
    val mod = (size + extra) % 4
    fixedSizeBytes(size, valueCodec).flatMap(a =>
      ignore((4 - mod) * 8)
        .map(_ => a))
  }

  def sizeBound: SizeBound = byte.sizeBound.atLeast

  override def encode(a: A): Attempt[BitVector] = for {
    encA <- valueCodec.encode(a)
    encSize <- TlBytesSizeCodec.encode(encA.bytes.size.intValue())
    alignment = BitVector(Array.fill(4 - ((encA.bytes.size + encSize.bytes.size) % 4).intValue())(0.toByte))
  } yield encSize ++ encA ++ alignment

  override def decode(buffer: BitVector): Attempt[DecodeResult[A]] = decoder.decode(buffer)

  override def toString = s"tlBytesCodec($valueCodec)"
}
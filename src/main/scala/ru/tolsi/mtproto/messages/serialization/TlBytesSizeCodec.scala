package ru.tolsi.mtproto.messages.serialization

import java.nio.ByteBuffer

import scodec.Attempt.Successful
import scodec.{Attempt, Codec, DecodeResult, Decoder, SizeBound}
import scodec.bits.BitVector
import scodec.codecs.{byte, sizedList}

object TlBytesSizeCodec extends Codec[Int] {
  override def encode(value: Int): Attempt[BitVector] = {
    // todo check max value?
    if (value >= 254) {
      val bb = ByteBuffer.allocate(4)
      bb.put(254.toByte)
      bb.put((value & 0xff).toByte)
      bb.put(((value >> 8) & 0xff).toByte)
      bb.put(((value >> 16) & 0xff).toByte)
      Successful(BitVector(bb.array()))
    } else {
      Successful(BitVector(Array(value.toByte)))
    }
  }

  override def sizeBound: SizeBound = byte.sizeBound.atLeast

  override def decode(bits: BitVector): Attempt[DecodeResult[Int]] = {
    (byte flatMap { first =>
      val len = getIntFromByte(first)
      if (len >= 254 || len == 0) {
        sizedList(3, byte).map { sizeBytes => {
          val res = getIntFromByte(sizeBytes(0)) | (getIntFromByte(sizeBytes(1)) << 8) | (getIntFromByte(sizeBytes(2)) << 16)
          res
        }}
      } else {
        Decoder.point(len)
      }
    }).decode(bits)
  }

  private def getIntFromByte(b: Byte): Int = if (b >= 0) b else b.toInt + 256

  override def toString: String = s"tlBytesSizeCodec"
}
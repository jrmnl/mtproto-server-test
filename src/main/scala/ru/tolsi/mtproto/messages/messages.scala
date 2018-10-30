package ru.tolsi.mtproto

import java.math.BigInteger
import java.nio.ByteBuffer

import ru.tolsi.mtproto.util.{ByteString, _}
import scodec.Attempt.Successful
import scodec._
import scodec.bits.{BitVector, ByteVector}
import scodec.codecs._
import shapeless.HNil

package object messages {

  trait MTProtoRequestMessage

  trait MTProtoResponceMessage

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

  final object TgBytesSizeCodec extends Codec[Int] {
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
            getIntFromByte(sizeBytes(0)) | (getIntFromByte(sizeBytes(1)) << 8) | (getIntFromByte(sizeBytes(2)) << 16)
          }
          }
        } else {
          Decoder.point(len)
        }
      }).decode(bits)
    }

    private def getIntFromByte(b: Byte): Int = {
      if (b >= 0) b else b.toInt + 256
    }
  }

  final class TlBytesCodec[A](valueCodec: Codec[A]) extends Codec[A] {
    private val decoder = TgBytesSizeCodec flatMap { size => fixedSizeBits(size, valueCodec) }

    def sizeBound: SizeBound = byte.sizeBound.atLeast

    override def encode(a: A): Attempt[BitVector] = for {
      encA <- valueCodec.encode(a)
      encSize <- TgBytesSizeCodec.encode(encA.bytes.size.intValue())
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

  def tlBytesCodec[A](value: Codec[A]) = new TlBytesCodec(value)

  def tlBytesString: Codec[ByteString] = tlBytesCodec(bytes).asByteString

  def tlBytesAsBigInt: Codec[BigInt] = tlBytesCodec(bytes).
    xmap(h => BigInt(1, h.toArray), bi => ByteVector(fromBigInt(bi.bigInteger)))
}

package ru.tolsi.mtproto.messages

import ru.tolsi.mtproto.util.ByteString
import scodec.{Codec, Err}
import scodec.bits.ByteVector
import scodec.codecs._
import ru.tolsi.mtproto.util._
import scodec.Attempt.Failure


package object serialization {

  def bytesString(n: Int): Codec[ByteString] = bytes(n).asByteString

  def bytesString: Codec[ByteString] = bytes.asByteString

  def tlBytesCodec[A](value: Codec[A]) = new TlBytesCodec(value)

  def tlBytesString: Codec[ByteString] = tlBytesCodec(bytes).asByteString

  def tlBytesAsBigInt: Codec[BigInt] = tlBytesCodec(bytes).xmap(h => BigInt(h.toArray), bi => ByteVector(bi.bigInteger.toByteArray))

  def tlObject: Codec[MTProtoObject] = variableSizeBytes(int32L, bytes).exmap[MTProtoObject](bv => {
    def decode[T <: MTProtoObject](codec: Codec[T]) = codec.decode(bv.bits).map(_.value)
    int32L.decode(bv.bits).flatMap {
      case scodec.DecodeResult(value, _) => value match {
        case ReqPq.classId => decode(codecs.reqPqCodec)
        case ReqDHParams.classId => decode(codecs.reqDHParamsCodec)
        case _ => Failure(Err(s"Can't deserialize message with code 0x${bv.take(4).toHex}"))
      }
    }
  }, {
    case resPq: ResPq => codecs.resPqCodec.encode(resPq).map(_.bytes)
    case m => Failure(Err(s"Can't serialize message $m"))
  })
}

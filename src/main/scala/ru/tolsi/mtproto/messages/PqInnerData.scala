package ru.tolsi.mtproto.messages

import ru.tolsi.mtproto.util.ByteString
import scodec.Codec
import scodec.codecs._
import ru.tolsi.mtproto._

object PqInnerData {
  val classId: Int = 0x83c95aec

  val pqInnerDataCodec: Codec[PqInnerData] = {
    ("constructor number" | constant(int32L.encode(classId).require)) ::
      ("pq" | tlBytesAsBigInt) ::
      ("p" | tlBytesAsBigInt) ::
      ("q" | tlBytesAsBigInt) ::
      ("nonce" | bytesString(16)) ::
      ("serverNonce" | bytesString(16)) ::
      ("newNonce" | bytesString(32))
  }.dropUnits.as[PqInnerData]
}

case class PqInnerData(pq: BigInt, p: BigInt, q: BigInt, nonce: ByteString, serverNonce: ByteString, newNonce: ByteString)

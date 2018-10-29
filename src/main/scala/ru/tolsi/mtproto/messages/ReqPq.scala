package ru.tolsi.mtproto.messages

import ru.tolsi.mtproto.util.ByteString
import scodec.Codec
import scodec.codecs._


object ReqPq {
  val classId: Int = 0x60469778
  val reqPqCodec: Codec[ReqPq] = {
    ("constructor number" | constant(int32L.encode(classId).require)) ::
      ("nonce" | bytesString(16))
  }.dropUnits.as[ReqPq]
}

case class ReqPq(nonce: ByteString) extends MTProtoRequestMessage
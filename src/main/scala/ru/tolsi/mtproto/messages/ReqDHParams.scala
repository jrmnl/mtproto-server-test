package ru.tolsi.mtproto.messages

import ru.tolsi.mtproto.util.ByteString
import scodec.Codec
import scodec.codecs._

object ReqDHParams {
  val classId: Int = 0xd712e4be

  val reqDHParamsCodec: Codec[ReqDHParams] = {
    ("constructor number" | constant(int32L.encode(classId).require)) ::
      ("nonce" | bytesString(16)) ::
      ("server_nonce" | bytesString(16)) ::
      ("p" | tlBytesAsBigInt) ::
      ("q" | tlBytesAsBigInt) ::
      ("public_key_fingerprint" | int64) ::
      ("encrypted_data" | EncyptedPqInnerData.encryptedPqInnerDataCodec)
  }.dropUnits.as[ReqDHParams]
}

case class ReqDHParams(nonce: ByteString, serverNonce: ByteString, p: BigInt, q: BigInt, fingerPrint: Long, encryptedData: EncyptedPqInnerData) extends MTProtoRequestMessage

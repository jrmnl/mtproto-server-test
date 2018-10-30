package ru.tolsi.mtproto.messages

import ru.tolsi.mtproto.util.ByteString

object ReqDHParams {
  val classId: Int = 0xd712e4be
}

case class ReqDHParams(nonce: ByteString, serverNonce: ByteString, p: BigInt, q: BigInt, fingerPrint: Long, encryptedData: EncyptedPqInnerData) extends MTProtoRequestMessage

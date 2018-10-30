package ru.tolsi.mtproto.messages

import ru.tolsi.mtproto.util.ByteString

object PqInnerData {
  val classId: Int = 0x83c95aec
}

case class PqInnerData(pq: BigInt, p: BigInt, q: BigInt, nonce: ByteString, serverNonce: ByteString, newNonce: ByteString)

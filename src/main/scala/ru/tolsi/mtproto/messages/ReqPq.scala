package ru.tolsi.mtproto.messages

import ru.tolsi.mtproto.util.ByteString


object ReqPq {
  val classId: Int = 0x60469778
}

case class ReqPq(nonce: ByteString) extends MTProtoRequestMessage
package ru.tolsi.mtproto.messages

import scodec.bits.ByteVector

case class UnencryptedMessage(authKeyId: Long, messageId: Long, messageDataLength: Int, messageData: ByteVector) {
  def parseMessage: MTProtoRequestMessage = ???
}
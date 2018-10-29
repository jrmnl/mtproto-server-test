package ru.tolsi.mtproto.messages

import scodec.Codec
import scodec.bits.ByteVector
import scodec.codecs._

object UnencryptedMessage {
  val unencryptedMessageCodec: Codec[UnencryptedMessage] = {
    ("auth_key_id" | int64) ::
      ("message_id" | int64) ::
      ("message_data_length" | int32) ::
      ("message_data" | bytes)
  }.as[UnencryptedMessage]
}

case class UnencryptedMessage(authKeyId: Long, messageId: Long, messageDataLength: Int, messageData: ByteVector) {
  def parseMessage: MTProtoRequestMessage = ???
}
package ru.tolsi.mtproto.messages

case class UnencryptedMessage(authKeyId: Long, messageId: Long, message: MTProtoObject)
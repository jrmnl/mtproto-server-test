package ru.tolsi.mtproto

import scodec._
import scodec.codecs._

sealed trait MTProtoInMessage
sealed trait MTProtoOutMessage

object UnencryptedMessage {
  val unencryptedMessageCodec: Codec[UnencryptedMessage] = {
    ("auth_key_id" | int64) ::
      ("message_id" | int64) ::
      ("message_data_length" | int32) ::
      ("message_data" | bytes)
  }.as[UnencryptedMessage]
}

case class UnencryptedMessage(authKeyId: Long, messageId: Long, messageDataLength: Int, messageData: Array[Byte]) {
  def parseMessage: MTProtoInMessage = ???
}

object ReqPq {
  val reqPqCodec: Codec[ReqPq] = {
    "nonce" | bytes(128)
  }.as[ReqPq]
}

case class ReqPq(nonce: Array[Byte]) extends MTProtoInMessage

object ResPq {
  def createRandom: ResPq = {
    ResPq(
      createRandomBytes(128), // int128
      createRandomBytes(128), // int128
      createRandomString(64), // bytes
      createRandomBytes(4 + 2 * 8)) // long vector = int + (n * long)
  }

  val resPqCodec: Codec[ResPq] = {
    ("nonce" | bytes(128)) ::
      ("pq" | ascii32) ::
      ("server_nonce" | bytes(128)) ::
      ("fingerprints" | bytes)
  }.as[ResPq]
}

case class ResPq(nonce: Array[Byte], serverNonce: Array[Byte], pq: String, fingerprints: Array[Byte]) extends MTProtoOutMessage

object ReqDHParams {
  val reqDHParamsCodec: Codec[ReqDHParams] = {
    ("nonce" | bytes(128)) ::
      ("server_nonce" | bytes(128)) ::
      ("p" | bytes(128)) ::
      ("q" | bytes(128)) ::
      ("finger_print" | int64) ::
      ("encrypted_data" | bytes)
  }.as[ReqDHParams]
}

case class ReqDHParams(nonce: Array[Byte], serverNonce: Array[Byte], p: Array[Byte], q: Array[Byte], fingerPrint: Long, encryptedData: Array[Byte]) extends MTProtoInMessage
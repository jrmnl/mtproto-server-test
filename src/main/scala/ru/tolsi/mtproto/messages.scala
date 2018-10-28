package ru.tolsi.mtproto

import java.nio.ByteBuffer
import java.security.interfaces.RSAPublicKey

import scodec._
import scodec.bits.{ByteVector, _}
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

case class UnencryptedMessage(authKeyId: Long, messageId: Long, messageDataLength: Int, messageData: ByteVector) {
  def parseMessage: MTProtoInMessage = ???
}

object ReqPq {
  val classId: Int = 0x60469778
  val reqPqCodec: Codec[ReqPq] = {
    ("constructor number" | constant(int32L.encode(classId).require)) ::
      ("nonce" | bytesString(16))
  }.dropUnits.as[ReqPq]
}

case class ReqPq(nonce: ByteString) extends MTProtoInMessage

object ResPq {
  val classId: Int = 0x05162463

  def createRandom: ResPq = {
    ResPq(
      ByteString(createRandomBytes(16)), // int128
      ByteString(createRandomBytes(16)), // int128
      ByteString(createRandomString(64).getBytes), // bytes
      ByteString(createRandomBytes(4 + 2 * 8))) // long vector = int + (n * long)
  }

  val resPqCodec: Codec[ResPq] = {
    ("constructor number" | constant(int32L.encode(classId).require)) ::
      ("nonce" | bytesString(16)) ::
      ("server_nonce" | bytesString(16)) ::
      ("pq" | tlBytesString) ::
      // todo vector https://core.telegram.org/mtproto/samples-auth_key
      ("fingerprints" | bytesString)
  }.dropUnits.as[ResPq]
}

case class ResPq(nonce: ByteString, serverNonce: ByteString, pq: ByteString, fingerprints: ByteString) extends MTProtoOutMessage

object ReqDHParams {
  val classId: Int = 0xd712e4be

  val reqDHParamsCodec: Codec[ReqDHParams] = {
    ("constructor number" | constant(int32L.encode(classId).require)) ::
      ("nonce" | bytesString(16)) ::
      ("server_nonce" | bytesString(16)) ::
      ("p" | tlBytesAsBigInt) ::
      ("q" | tlBytesAsBigInt) ::
      ("finger_print" | int64) ::
      ("encrypted_data" | EncyptedPqInnerData.encryptedPqInnerDataCodec)
  }.dropUnits.as[ReqDHParams]
}

case class ReqDHParams(nonce: ByteString, serverNonce: ByteString, p: BigInt, q: BigInt, fingerPrint: Long, encryptedData: EncyptedPqInnerData) extends MTProtoInMessage

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

object EncyptedPqInnerData {
  def encrypt(innerData: PqInnerData, key: RSAPublicKey): EncyptedPqInnerData = {
    val serialized = PqInnerData.pqInnerDataCodec.encode(innerData).require.toByteArray // 96 bytes
    val hash = crypto.sha1(serialized) // 20 bytes
    val seed = createRandomBytes(255 - hash.length - serialized.length) // 139 bytes

    val byteBuffer = ByteBuffer.allocate(255)
    byteBuffer.put(hash)
    byteBuffer.put(seed)
    byteBuffer.put(serialized)
    val data = byteBuffer.array()

    EncyptedPqInnerData(ByteString(crypto.rsa(data, key)))
  }

  val encryptedPqInnerDataCodec: Codec[EncyptedPqInnerData] = {
    "encrypted_bytes" | bytesString(16)
  }.as[EncyptedPqInnerData]
}

case class EncyptedPqInnerData(byteString: ByteString)
package ru.tolsi.mtproto.messages.serialization

import ru.tolsi.mtproto.crypto.rsa.RSAPublicKey
import ru.tolsi.mtproto.messages._
import scodec.Codec
import scodec.codecs._
import ru.tolsi.mtproto.messages.{RSAPublicKey => RSAPublicKeyMessage}

object codecs {
  val encryptedPqInnerDataCodec: Codec[EncyptedPqInnerData] = {
    "encrypted_bytes" | tlBytesString
  }.as[EncyptedPqInnerData]

  val pqInnerDataCodec: Codec[PqInnerData] = {
    ("constructor number" | constant(int32L.encode(PqInnerData.classId).require)) ::
      ("pq" | tlBytesAsBigInt) ::
      ("p" | tlBytesAsBigInt) ::
      ("q" | tlBytesAsBigInt) ::
      ("nonce" | bytesString(16)) ::
      ("serverNonce" | bytesString(16)) ::
      ("newNonce" | bytesString(32))
  }.dropUnits.as[PqInnerData]

  val reqDHParamsCodec: Codec[ReqDHParams] = {
    ("constructor number" | constant(int32L.encode(ReqDHParams.classId).require)) ::
      ("nonce" | bytesString(16)) ::
      ("server_nonce" | bytesString(16)) ::
      ("p" | tlBytesAsBigInt) ::
      ("q" | tlBytesAsBigInt) ::
      ("public_key_fingerprint" | int64L) ::
      ("encrypted_data" | encryptedPqInnerDataCodec)
  }.dropUnits.as[ReqDHParams]

  val resPqCodec: Codec[ResPq] = {
    ("constructor number" | constant(int32L.encode(ResPq.classId).require)) ::
      ("nonce" | bytesString(16)) ::
      ("server_nonce" | bytesString(16)) ::
      ("pq" | tlBytesAsBigInt) ::
      ("fingerprints" | tlVectorCodec(int64L))
  }.dropUnits.as[ResPq]

  val reqPqCodec: Codec[ReqPq] = {
    ("constructor number" | constant(int32L.encode(ReqPq.classId).require)) ::
      ("nonce" | bytesString(16))
  }.dropUnits.as[ReqPq]

  val rsaPublicKeyCodec: Codec[RSAPublicKey] = {
    ("constructor number" | constant(int32L.encode(RSAPublicKeyMessage.classId).require)) ::
      ("modulus" | tlBytesAsBigInt) ::
      ("exponent" | tlBytesAsBigInt)
  }.dropUnits.as[RSAPublicKey]

  val unencryptedMessageCodec: Codec[UnencryptedMessage] = {
    ("auth_key_id" | int64L) ::
      ("message_id" | int64L) ::
      ("message_data_length" | int32L) ::
      ("message_data" | bytes)
  }.as[UnencryptedMessage]

  def tlVectorCodec[T](valueC: Codec[T]): Codec[TlVector[T]] = {
    ("constructor number" | constant(int32L.encode(TlVector.classId).require)) ::
      ("values" | listOfN(int32L, valueC))
  }.dropUnits.as[TlVector[T]]
}

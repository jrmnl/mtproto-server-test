package ru.tolsi.mtproto.messages

import java.nio.ByteBuffer

import ru.tolsi.mtproto.crypto.SHA1
import ru.tolsi.mtproto.crypto.rsa.{RSAPrivateKey, RSAPublicKey}
import ru.tolsi.mtproto.util._
import scodec.{Attempt, Codec, DecodeResult}
import scodec.bits.BitVector
import scodec.codecs._


object EncyptedPqInnerData {
  def encryptData(innerData: PqInnerData): Array[Byte] = {
    val serialized = PqInnerData.pqInnerDataCodec.encode(innerData).require.toByteArray // 96 bytes
    val hash = SHA1.hash(serialized) // 20 bytes
    val seed = createRandomBytes(255 - hash.length - serialized.length) // 139 bytes

    val byteBuffer = ByteBuffer.allocate(255)
    byteBuffer.put(hash)
    byteBuffer.put(serialized)
    byteBuffer.put(seed)
    byteBuffer.array()
  }

  def encrypt(innerData: PqInnerData, key: RSAPublicKey): EncyptedPqInnerData = {
    val encrypted = key.encrypt(encryptData(innerData))
    EncyptedPqInnerData(ByteString(encrypted))
  }

  def decrypt(encrypted: Array[Byte], key: RSAPrivateKey): Attempt[DecodeResult[PqInnerData]] = {
    val decrypted = key.decrypt(encrypted)
    // todo validate?
    // original data had 255 bytes, but decrypted data has 256 bytes
    // val hash = decrypted.drop(1).take(20)
    val data = decrypted.slice(21, 117)
    // val seed = decrypted.drop(117)
    PqInnerData.pqInnerDataCodec.decode(BitVector(data))
  }

  val encryptedPqInnerDataCodec: Codec[EncyptedPqInnerData] = {
    "encrypted_bytes" | tlBytesString
  }.as[EncyptedPqInnerData]
}

case class EncyptedPqInnerData(encrypted: ByteString)
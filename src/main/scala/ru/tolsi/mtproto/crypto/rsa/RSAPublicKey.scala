package ru.tolsi.mtproto.crypto.rsa

import java.security.interfaces.{RSAPublicKey => JavaRSAPublicKey}
import java.security.spec.RSAPublicKeySpec

import javax.crypto.Cipher
import ru.tolsi.mtproto.crypto.SHA1
import ru.tolsi.mtproto.messages.serialization.codecs

object RSAPublicKey {
  def apply(javaRSAPublicKey: JavaRSAPublicKey): RSAPublicKey = new RSAPublicKey(
    javaRSAPublicKey.getModulus,
    javaRSAPublicKey.getPublicExponent)

}
case class RSAPublicKey(publicKey: BigInt, exponent: BigInt) {

  def asJavaRSAKey(): JavaRSAPublicKey = {
    rsaKeyFactory.generatePublic(new RSAPublicKeySpec(publicKey.bigInteger, exponent.bigInteger)).asInstanceOf[JavaRSAPublicKey]
  }

  def fingerprint: Long = {
    val serialized = codecs.rsaPublicKeyCodec.encode(this).require.toByteArray
    val bytes = SHA1.hash(serialized).takeRight(8)
    bytesToLong(bytes)
  }

  def encrypt(message: Array[Byte]): Array[Byte] = {
    rsaECBcipher.init(Cipher.ENCRYPT_MODE, asJavaRSAKey())
    rsaECBcipher.doFinal(message)
  }
}
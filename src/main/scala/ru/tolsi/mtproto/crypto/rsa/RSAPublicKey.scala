package ru.tolsi.mtproto.crypto.rsa

import java.nio.ByteBuffer
import java.security.interfaces.{RSAPublicKey => JavaRSAPublicKey}
import java.security.spec.RSAPublicKeySpec

import javax.crypto.Cipher
import ru.tolsi.mtproto.crypto.SHA1


object RSAPublicKey {
  def apply(javaRSAPublicKey: JavaRSAPublicKey): RSAPublicKey = new RSAPublicKey(
    BigInt(1, javaRSAPublicKey.getEncoded),
    javaRSAPublicKey.getPublicExponent,
    ByteBuffer.wrap(SHA1.hash(javaRSAPublicKey.getEncoded).take(8)).getLong)
}
case class RSAPublicKey(publicKey: BigInt, exponent: BigInt, fingerprint: Long) {
  def asJavaRSAKey(): JavaRSAPublicKey = {
    rsaKeyFactory.generatePublic(new RSAPublicKeySpec(publicKey.bigInteger, exponent.bigInteger)).asInstanceOf[JavaRSAPublicKey]
  }

  def encrypt(message: Array[Byte]): Array[Byte] = {
    rsaECBcipher.init(Cipher.ENCRYPT_MODE, asJavaRSAKey())
    rsaECBcipher.doFinal(message)
  }
}
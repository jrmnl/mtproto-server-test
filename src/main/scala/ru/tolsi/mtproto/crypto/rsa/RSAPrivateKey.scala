package ru.tolsi.mtproto.crypto.rsa

import java.security.interfaces.{RSAPrivateKey => JavaRSAPrivateKey}

import javax.crypto.Cipher

case class RSAPrivateKey(privateKey: JavaRSAPrivateKey) {
  def decrypt(encrypted: Array[Byte]): Array[Byte] = {
    rsaECBcipher.init(Cipher.DECRYPT_MODE, privateKey)
    rsaECBcipher.doFinal(encrypted)
  }
}

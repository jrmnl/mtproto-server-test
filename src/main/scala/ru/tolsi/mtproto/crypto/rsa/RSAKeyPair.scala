package ru.tolsi.mtproto.crypto.rsa

import java.security.{KeyPairGenerator, SecureRandom}
import java.security.interfaces.{RSAPrivateKey => JavaRSAPrivateKey, RSAPublicKey => JavaRSAPublicKey}

object RSAKeyPair {
  def generate(): RSAKeyPair = {
    val kpg = KeyPairGenerator.getInstance("RSA")
    kpg.initialize(2048, new SecureRandom())
    val pair = kpg.generateKeyPair()
    RSAKeyPair(RSAPrivateKey(pair.getPrivate.asInstanceOf[JavaRSAPrivateKey]), RSAPublicKey(pair.getPublic.asInstanceOf[JavaRSAPublicKey]))
  }
}

case class RSAKeyPair(privateKey: RSAPrivateKey, publicKey: RSAPublicKey)

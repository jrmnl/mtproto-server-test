package ru.tolsi.mtproto.crypto

import java.nio.charset.StandardCharsets

import org.scalatest.{Matchers, WordSpec}
import ru.tolsi.mtproto.crypto.rsa.RSAKeyPair
import ru.tolsi.mtproto.util.ByteString

class RSASpec extends WordSpec with Matchers {
  "RSA" should {
    "roundtrip data" in {
      val message = "a" * 50
      val messageBytes = message.getBytes(StandardCharsets.UTF_8)
      val kp = RSAKeyPair.generate()
      val encrypted = kp.publicKey.encrypt(messageBytes)
      val decrypted = kp.privateKey.decrypt(encrypted)
      decrypted should have size 256
      ByteString(decrypted.dropWhile(_ == 0)) should be(ByteString(messageBytes))
    }
  }
}

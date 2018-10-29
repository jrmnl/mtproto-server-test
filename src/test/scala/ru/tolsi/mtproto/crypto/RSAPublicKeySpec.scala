package ru.tolsi.mtproto.crypto

import org.scalatest.{Matchers, WordSpec}
import ru.tolsi.mtproto.crypto.rsa.RSAPublicKey

class RSAPublicKeySpec extends WordSpec with Matchers {

  "RSA Public key" should {
    "calculate valid fingerprint" in {
      val rsaKey = RSAPublicKey(BigInt("0c150023e2f70db7985ded064759cfecf0af328e69a41daf4d6f01b538135a6f91f8f8b2a0ec9ba9720ce352efcf6c5680ffc424bd634864902de0b4bd6d49f4e580230e3ae97d95c8b19442b3c0a10d8f5633fecedd6926a7f6dab0ddb7d457f9ea81b8465fcd6fffeed114011df91c059caedaf97625f6c96ecc74725556934ef781d866b34f011fce4d835a090196e9a5f0e4449af7eb697ddb9076494ca5f81104a305b6dd27665722c46b60e5df680fb16b210607ef217652e60236c255f6a28315f4083a96791d7214bf64c1df4fd0db1944fb26a2a57031b32eee64ad15a8ba68885cde74a5bfc920f6abf59ba5c75506373e7130f9042da922179251f", 16), BigInt("010001", 16))
      val fingerprint = 0xc3b42b026ce86b21L
      rsaKey.fingerprint should be(fingerprint)
    }
  }
}

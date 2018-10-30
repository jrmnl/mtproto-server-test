package ru.tolsi.mtproto.messages

import ru.tolsi.mtproto.crypto.rsa.{RSAPublicKey => CryptoRSAPublicKey}
import scodec.Codec
import scodec.codecs._

object RSAPublicKey {
  val classId: Int = 0x7a19cb76

  val rsaPublicKeyCodec: Codec[CryptoRSAPublicKey] = {
    ("constructor number" | constant(int32L.encode(classId).require)) ::
      ("modulus" | tlBytesAsBigInt) ::
      ("exponent" | tlBytesAsBigInt)
  }.dropUnits.as[CryptoRSAPublicKey]
}

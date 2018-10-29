package ru.tolsi.mtproto.crypto

import java.security.KeyFactory

import javax.crypto.Cipher

package object rsa {
  private[rsa] val rsaKeyFactory: KeyFactory = KeyFactory.getInstance("RSA")
  private[rsa] val rsaECBcipher = Cipher.getInstance("RSA/ECB/NoPadding")
}

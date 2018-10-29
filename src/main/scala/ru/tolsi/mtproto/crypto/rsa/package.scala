package ru.tolsi.mtproto.crypto

import java.security.KeyFactory

import javax.crypto.Cipher

package object rsa {
  private[rsa] val rsaKeyFactory: KeyFactory = KeyFactory.getInstance("RSA")
  private[rsa] val rsaECBcipher = Cipher.getInstance("RSA/ECB/NoPadding")

  def bytesToLong(bytes: Array[Byte]): Long = (bytes(7).toLong << 56) + ((bytes(6).toLong & 0xFF) << 48) + ((bytes(5).toLong & 0xFF) << 40) + ((bytes(4).toLong & 0xFF) << 32) + ((bytes(3).toLong & 0xFF) << 24) + ((bytes(2).toLong & 0xFF) << 16) + ((bytes(1).toLong & 0xFF) << 8) + (bytes(0).toLong & 0xFF)
}

package ru.tolsi.mtproto.crypto

object SHA1 {
  private val sha1md = java.security.MessageDigest.getInstance("SHA-1")

  def hash(bytes: Array[Byte]): Array[Byte] = {
    sha1md.digest(bytes)
  }
}

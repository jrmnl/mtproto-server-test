package ru.tolsi.mtproto.util

import java.math.BigInteger

object ByteString {
  def fromHex(hex: String): ByteString = ByteString(new BigInteger(hex, 16).toByteArray)
}

case class ByteString(arr: Array[Byte]) {
  override def equals(a: Any): Boolean = a match {
    case other: ByteString => arr.sameElements(other.arr)
    case _ => false
  }

  override def hashCode(): Int = java.util.Arrays.hashCode(arr)

  def hex: String = arr.toHex

  override def toString: String = hex
}

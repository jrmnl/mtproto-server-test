package ru.tolsi.mtproto.util

case class ByteString(arr: Array[Byte]) {
  override def equals(a: Any): Boolean = a match {
    case other: ByteString => arr.sameElements(other.arr)
    case _              => false
  }

  override def hashCode(): Int = java.util.Arrays.hashCode(arr)

  def hex: String = arr.toHex

  override def toString: String = hex
}

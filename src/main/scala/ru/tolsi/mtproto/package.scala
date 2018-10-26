package ru.tolsi

import scala.util.Random

package object mtproto {
  private val r = new Random()
  def createRandomString(n: Int): String = r.alphanumeric.take(n).mkString
  def createRandomBytes(n: Int): Array[Byte] = {
    val a = new Array[Byte](n)
    r.nextBytes(a)
    a
  }
}

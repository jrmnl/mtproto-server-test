package ru.tolsi.mtproto.messages

object TlVector {
  val classId: Int = 0x1cb5c415
}
case class TlVector[T](list: List[T])

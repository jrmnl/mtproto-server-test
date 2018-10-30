package ru.tolsi.mtproto

package object messages {
  sealed trait MTProtoObject
  trait MTProtoRequestMessage extends MTProtoObject
  trait MTProtoResponseMessage extends MTProtoObject
}

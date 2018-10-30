package ru.tolsi.mtproto.messages

import ru.tolsi.mtproto.util._

object ResPq {
  val classId: Int = 0x05162463

  def createRandom: ResPq = {
    ResPq(
      ByteString(createRandomBytes(16)), // int128
      ByteString(createRandomBytes(16)), // int128
      createRandomBigInt(64), // bytes
      ByteString(createRandomBytes(4 + 2 * 8))) // long vector = int + (n * long)
  }
}

case class ResPq(nonce: ByteString, serverNonce: ByteString, pq: BigInt, fingerprints: ByteString) extends MTProtoResponseMessage

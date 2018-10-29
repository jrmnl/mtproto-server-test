package ru.tolsi.mtproto.messages

import ru.tolsi.mtproto.util._
import scodec.Codec
import scodec.codecs._

import scala.util.Random

object ResPq {
  val classId: Int = 0x05162463

  def createRandom: ResPq = {
    ResPq(
      ByteString(createRandomBytes(16)), // int128
      ByteString(createRandomBytes(16)), // int128
      createRandomBigInt(64), // bytes
      ByteString(createRandomBytes(4 + 2 * 8))) // long vector = int + (n * long)
  }

  val resPqCodec: Codec[ResPq] = {
    ("constructor number" | constant(int32L.encode(classId).require)) ::
      ("nonce" | bytesString(16)) ::
      ("server_nonce" | bytesString(16)) ::
      ("pq" | tlBytesAsBigInt) ::
      // todo it should be a vector
      ("fingerprints" | bytesString)
  }.dropUnits.as[ResPq]
}

case class ResPq(nonce: ByteString, serverNonce: ByteString, pq: BigInt, fingerprints: ByteString) extends MTProtoResponceMessage

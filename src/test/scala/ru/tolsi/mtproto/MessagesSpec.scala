package ru.tolsi.mtproto

import org.scalatest.{Matchers, WordSpec}
import scodec.bits._

class MessagesSpec extends WordSpec with Matchers {

  private val p = BigInt("1599411841")
  private val q = BigInt("1913360507")
  private val pq = p * q
  private val nonce = ByteString(hex"3e0549828cca27e966b301a48fece2fc".toArray)
  private val serverNonce = ByteString(hex"dc714e5c6937631e37ce313f3b95ab0e".toArray)
  private val newNonce = ByteString(hex"406073d8d586a504a73dd63ea2357703b2e258bec4216c51b56554fc634afea2".toArray)


  private val pqInnerData = PqInnerData(pq = pq, p = p, q = q, nonce = nonce, serverNonce = serverNonce, newNonce = newNonce)

  val rsaKey = crypto.publicRSAKeys.head

  "ReqPq codec" should {
    "encode a valid message" in {
      val blob = hex"789746603e0549828cca27e966b301a48fece2fc".bits
      val message = ReqPq(nonce)
      ReqPq.reqPqCodec.encode(message).require should be(blob)
    }

    "decode a valid message" in {
      val blob = hex"789746603e0549828cca27e966b301a48fece2fc".bits
      val message = ReqPq(nonce)
      ReqPq.reqPqCodec.decode(blob).require.value should be(message)
    }
  }

  "PqInnerData codec" should {
    "encode a valid message" in {
      val blob =
        hex"ec5ac983082a78327b14155ffb000000045f55168100000004720b907b0000003e0549828cca27e966b301a48fece2fcdc714e5c6937631e37ce313f3b95ab0e406073d8d586a504a73dd63ea2357703b2e258bec4216c51b56554fc634afea2".toArray
      PqInnerData.pqInnerDataCodec.encode(pqInnerData).require.toByteArray should be(blob)
    }

    "decode a valid message" in {
      val blob =
        hex"ec5ac983082a78327b14155ffb000000045f55168100000004720b907b0000003e0549828cca27e966b301a48fece2fcdc714e5c6937631e37ce313f3b95ab0e406073d8d586a504a73dd63ea2357703b2e258bec4216c51b56554fc634afea2".bits
      PqInnerData.pqInnerDataCodec.decode(blob).require.value should be(pqInnerData)
    }
  }

  "ReqDHParams codec" should {
    "encode a valid message" in {
      val pqInnerData = PqInnerData(pq = pq, p = p, q = q, nonce = nonce, serverNonce = serverNonce, newNonce = newNonce)

      val req = ReqDHParams(nonce, serverNonce, p, q, rsaKey.fingerprint, EncyptedPqInnerData.encrypt(pqInnerData, rsaKey.asRSAKey()))
      val blob =
        hex"bee412d73e0549828cca27e966b301a48fece2fcdc714e5c6937631e37ce313f3b95ab0e045f55168100000004720b907b00000089940ccd623014f7fe000100459f4203d001cd2d0d70339e8c86031a38f736051d02223a7e1c7eef559f1f6411965c79e6c15e7a1bcbc3ec7be43148cd1f3d63d5b7a9853dcc7784e25a3dc8b649d9470c1660074a85fe49328384868b6bd20e2d8265d120c7749bf57565068a973a9f0c0b149a7550f85b36f33b70a2686efccae13d5b082e5f0fa27a9a83507fef112b896997eb5a919858ca01c7f528c42b1175aaa267f540f61441177c7489ed5030e6217fbf0f0a22b8fba5aa7e29d84e720ad7c8f6ae62e5e7e8622c8eb33ff4cd26362195e45aad203dd87430db8359c68c1b57622ed4e1385ae4661f131c550ea08d540c287920dea8d1c258fe1233924ddd058fd889ba52c8c495".bits
      val b = ReqDHParams.reqDHParamsCodec.encode(req).require.toByteArray
      b should be(blob.toByteArray)
    }

    "decode a valid message" in {
      val req = ReqDHParams(nonce, serverNonce, p, q, rsaKey.fingerprint, EncyptedPqInnerData.encrypt(pqInnerData, rsaKey.asRSAKey()))
      val blob =
        hex"bee412d73e0549828cca27e966b301a48fece2fcdc714e5c6937631e37ce313f3b95ab0e045f55168100000004720b907b00000089940ccd623014f7fe000100459f4203d001cd2d0d70339e8c86031a38f736051d02223a7e1c7eef559f1f6411965c79e6c15e7a1bcbc3ec7be43148cd1f3d63d5b7a9853dcc7784e25a3dc8b649d9470c1660074a85fe49328384868b6bd20e2d8265d120c7749bf57565068a973a9f0c0b149a7550f85b36f33b70a2686efccae13d5b082e5f0fa27a9a83507fef112b896997eb5a919858ca01c7f528c42b1175aaa267f540f61441177c7489ed5030e6217fbf0f0a22b8fba5aa7e29d84e720ad7c8f6ae62e5e7e8622c8eb33ff4cd26362195e45aad203dd87430db8359c68c1b57622ed4e1385ae4661f131c550ea08d540c287920dea8d1c258fe1233924ddd058fd889ba52c8c495".bits
      ReqDHParams.reqDHParamsCodec.decode(blob).require.value should be(req)
    }
  }

}

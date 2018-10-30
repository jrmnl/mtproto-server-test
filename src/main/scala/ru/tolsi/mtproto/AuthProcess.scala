package ru.tolsi.mtproto

import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import akka.stream.{Attributes, FlowShape, Inlet, Outlet}
import com.typesafe.scalalogging.StrictLogging
import ru.tolsi.mtproto.messages._

sealed trait AuthStep

case object WaitForReqPq extends AuthStep

case object WaitForReqDHParams extends AuthStep

case object Done extends AuthStep

class AuthProcess extends GraphStage[FlowShape[UnencryptedMessage, MTProtoResponseMessage]] with StrictLogging {

  val in: Inlet[UnencryptedMessage] = Inlet[UnencryptedMessage]("in")
  val out: Outlet[MTProtoResponseMessage] = Outlet[MTProtoResponseMessage]("out")

  override val shape: FlowShape[UnencryptedMessage, MTProtoResponseMessage] = FlowShape.of(in, out)

  override def createLogic(attr: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {
      private var step: AuthStep = WaitForReqPq
      setHandler(in, new InHandler {
        override def onPush(): Unit = {
          val msg = grab(in)
          authLogic(step, msg.message) match {
            case Right((nextStep, messageOpt)) =>
              if (nextStep == Done) {
                completeStage()
              } else {
                step = nextStep
                messageOpt.foreach(m => push(out, m))
              }
            case Left(error) =>
              logger.error(error)
              throw new IllegalArgumentException(s"Cannot process message '${msg.message}': $error")
          }
        }
      })
      setHandler(out, new OutHandler {
        override def onPull(): Unit = {
          pull(in)
        }
      })
    }

  private def authLogic(currentStep: AuthStep, message: MTProtoObject): Either[String, (AuthStep, Option[MTProtoResponseMessage])] = {
    currentStep match {
      case WaitForReqPq => message match {
        case ReqPq(nonce) => Right(WaitForReqDHParams -> Some(ResPq.createRandom.copy(nonce = nonce)))
        case _ => Left("I'm wait for ReqPq")
      }
      case WaitForReqDHParams => message match {
        case _: ReqDHParams => Right(Done -> None)
        case _ => Left("I'm wait for ReqDHParams")
      }
    }
  }
}

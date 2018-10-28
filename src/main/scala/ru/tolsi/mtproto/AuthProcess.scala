package ru.tolsi.mtproto

import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import akka.stream.{Attributes, FlowShape, Inlet, Outlet}
import com.typesafe.scalalogging.StrictLogging

sealed trait AuthStep

case object WaitForReqPq extends AuthStep

case object WaitForReqDHParams extends AuthStep

class AuthProcess extends GraphStage[FlowShape[MTProtoInMessage, MTProtoOutMessage]] with StrictLogging {

  val in: Inlet[MTProtoInMessage] = Inlet[MTProtoInMessage]("in")
  val out: Outlet[MTProtoOutMessage] = Outlet[MTProtoOutMessage]("out")

  override val shape: FlowShape[MTProtoInMessage, MTProtoOutMessage] = FlowShape.of(in, out)

  override def createLogic(attr: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {
      private var step: AuthStep = WaitForReqPq
      setHandler(in, new InHandler {
        override def onPush(): Unit = {
          val msg = grab(in)
          authLogic(step, msg) match {
            case Right((nextStep, message)) =>
              step = nextStep
              push(out, message)
            case Left(error) =>
              logger.info(error)
              completeStage()
          }
        }
      })
      setHandler(out, new OutHandler {
        override def onPull(): Unit = {
          pull(in)
        }
      })
    }

  private def authLogic(currentStep: AuthStep, message: MTProtoInMessage): Either[String, (AuthStep, MTProtoOutMessage)] = {
    currentStep match {
      case WaitForReqPq => message match {
        case _: ReqPq => Right(WaitForReqDHParams -> ResPq.createRandom)
        case _ => Left("I'm wait for ReqPq, not this")
      }
      case WaitForReqDHParams => message match {
        case _: ReqPq => Left("I'm wait for ReqDHParams, not this")
        case _: ReqDHParams => Left("Done!")
      }
    }
  }
}

package com.hlf

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import com.hlf.controller.BusinessDateController
import com.hlf.network.NetworkSetup
import com.hlf.service.BusinessDateService
import org.slf4j.LoggerFactory

import scala.io.StdIn

object MainApp extends App {

  implicit val system = ActorSystem(Behaviors.empty, "Api")
  implicit val executionContext = system.executionContext

  val logger = LoggerFactory.getLogger(MainApp.getClass)

  val contract = NetworkSetup.createContract()
  val businessDateService = new BusinessDateService(contract)

  val bindingFuture = Http()
    .newServerAt("localhost", 8080)
    .bind(new BusinessDateController(businessDateService).route)

  logger.warn("API started.")

  StdIn.readLine()

  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}

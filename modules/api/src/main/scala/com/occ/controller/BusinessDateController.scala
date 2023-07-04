package com.hlf.controller

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.StatusCodes.InternalServerError
import akka.http.scaladsl.server.Directives.{as, complete, entity, extractUri, handleExceptions, path, pathPrefix, post}
import akka.http.scaladsl.server.{Directives, ExceptionHandler, Route}
import com.hlf.controller.BusinessDateController.logger
import com.hlf.service.BusinessDateService
import org.slf4j.LoggerFactory

object BusinessDateController {
  val logger = LoggerFactory.getLogger(BusinessDateController.getClass)
}

class BusinessDateController(businessDateContract: BusinessDateService) {

  implicit def businessDateExceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case err: Throwable =>
        extractUri { uri =>
          logger.error(s"Request to $uri could not be handled normally", err)
          complete(InternalServerError)
        }
    }

  val route: Route =
    handleExceptions(businessDateExceptionHandler) {
      Directives.concat(
        Directives.get {
          pathPrefix("business-date") {
            complete(businessDateContract.fetchBusinessDate())
          }
        },
        post {
          path("business-date") {
            entity(as[String]) { date =>
              businessDateContract.saveBusinessDate(date)
              complete("OK")
            }
          }
        }
      )
    }
}

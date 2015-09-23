package io.keen.error

import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

object ErrorReportingAction extends ActionBuilder[Request] {

  def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
    Try(block(request)) match {
      case Success(r) => r
      case Failure(ex) => {
        Reporter.logException(request, ex)
        Future.successful(InternalServerError("NOK"))
      }
    }
  }
}

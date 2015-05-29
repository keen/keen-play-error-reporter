package utils

import scala.concurrent.Future
import scala.util.{Failure,Success,Try}

import play.api.mvc._
import play.api.mvc.BodyParsers._
import play.api.mvc.Results._

import io.keen.error.Reporter

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

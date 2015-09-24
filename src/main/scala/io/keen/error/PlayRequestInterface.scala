package io.keen.error

import net.kencochrane.raven.event.interfaces.SentryInterface
import play.api.Logger
import play.api.mvc._

class PlayRequestInterface(request: Request[Any]) extends SentryInterface {

  def getHeaders: Headers = request.headers

  override def getInterfaceName: String = "sentry.interfaces.Http"

  def getBody: String = request.body match {
    case c: AnyContentAsJson => c.asJson.getOrElse("").toString
    case c: AnyContentAsText => c.asText.getOrElse("N/A")
    case _ => {
      Logger.warn("Failed to stringify body of ${request.body.getClass}")
      "N/A"
    }
  }

  def getMethod: String = request.method

  // Note: We filter out api_key
  def getParameters: String = request.queryString.filter(_._1 != "api_key").toString

  def getRequestUrl: String = request.uri

  def getRemoteAddr: String = request.remoteAddress

  def isSecure: String = request.secure.toString
}

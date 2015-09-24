package io.keen.error

import com.fasterxml.jackson.core.JsonGenerator
import net.kencochrane.raven.dsn.Dsn
import net.kencochrane.raven.event.interfaces.ExceptionInterface
import net.kencochrane.raven.event.{Event, EventBuilder}
import net.kencochrane.raven.marshaller.json.InterfaceBinding
import play.api.Play.current
import play.api._
import play.api.mvc.Request

object Reporter {

  val config = Play.configuration

  val raven = config.getString("sentry.dsn").map({ dsn =>
    new CustomRavenFactory().createRavenInstance(new Dsn(dsn))
  })

  if(raven.isDefined) {
    Logger.info("Sending exceptions to Sentry is ENABLED!")
  } else {
    Logger.warn("Sending exceptions to Sentry is DISABLED!")
  }

  def logException(request: Request[Any], e: Throwable): Unit = {
    Logger.error("Caught exception", e)
    raven.map({ r =>
      val eventBuilder = new EventBuilder()
        .withMessage(e.getMessage)
        .withLevel(Event.Level.ERROR)
        .withSentryInterface(new ExceptionInterface(e))
        .withSentryInterface(new PlayRequestInterface(request))
        // .withTag("version", BuildInfo.version)
      r.runBuilderHelpers(eventBuilder)
      r.sendEvent(eventBuilder.build)
    })
  }
}

class PlayRequestInterfaceBinding extends InterfaceBinding[PlayRequestInterface] {

  val URL = "url"
  val DATA = "data"
  val QUERY_STRING = "query_string"
  val METHOD = "method"
  val HEADERS = "headers"
  val ENVIRONMENT = "env"
  val ENV_REMOTE_ADDR = "REMOTE_ADDR"
  val ENV_REQUEST_SECURE = "REQUEST_SECURE"

    // I couldn't find any real documentation on this except to poke around in the source
    // clients. The best I could find was this:
    // https://github.com/getsentry/sentry/blob/master/src/sentry/interfaces/http.py#L47
  override def writeInterface(generator: JsonGenerator, request: PlayRequestInterface): Unit = {
    generator.writeStartObject
    generator.writeStringField(URL, request.getRequestUrl)
    generator.writeStringField(QUERY_STRING, request.getParameters)
    generator.writeStringField(METHOD, request.getMethod)
    generator.writeStringField(DATA, request.getBody)

    generator.writeFieldName(HEADERS)
    generator.writeStartObject
    request.getHeaders.keys.map({ h =>
      if(h != "Authorization") { // Don't write auth keys
        generator.writeStringField(h, request.getHeaders.getAll(h).mkString(","))
      }
    })
    generator.writeEndObject

    generator.writeFieldName(ENVIRONMENT)
    generator.writeStartObject
    generator.writeStringField(ENV_REMOTE_ADDR, request.getRemoteAddr)
    generator.writeStringField(ENV_REQUEST_SECURE, request.isSecure)
    generator.writeEndObject

    generator.writeEndObject()
  }
}


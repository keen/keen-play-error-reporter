# Keen Play Error Reporter

This is a shared library that can take [Play Framework](https://www.playframework.com/) requests and send them to [Sentry](https://www.getsentry.com/welcome/).

# Notes

This library will log the exception with `Logger.error` before sending it to Sentry.

# Using It

In your build.sbt
```
libraryDependencies ++= Seq(
  // Other deps
  "io.keen"                 %% "keen-error-reporter"            % "1.0.0"
)
```

In your application.conf
```
sentry.dsn=XX_PUT_YOUR_DSN_HERE_XX
```

Note: If there is no `sentry.dsn` then reporting to sentry will be disabled, but the reporter will still log!

```scala
import io.keen.error.Reporter

// Catch your exception, then send it along
Reporter.logException(request, ex)
```

package json.transformation.layer

import zio.logging.backend.SLF4J
import zio.{Runtime, ZLayer}

object LoggingLayer {

  val live: ZLayer[Any, Nothing, Unit] =
    Runtime.removeDefaultLoggers >>> SLF4J.slf4j
}

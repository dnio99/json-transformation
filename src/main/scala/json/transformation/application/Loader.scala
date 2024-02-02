package json.transformation.application

import cats.data.Kleisli
import cats.effect.Async
import com.comcast.ip4s.{Host, Port}
import fs2.io.net.Network
import json.transformation.layer.AllEnv
import json.transformation.{Configuration, layer}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import org.http4s.{Request, Response}
import zio.config.getConfig
import zio.interop.catz.asyncInstance
import zio.{ExitCode, RIO, Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object Loader extends ZIOAppDefault {
  override def run: ZIO[Any with ZIOAppArgs with Scope, Throwable, Unit] = {

    val allRoutes: Kleisli[RIO[AllEnv, *], Request[RIO[AllEnv, *]], Response[
      RIO[AllEnv, *]
    ]] = Router(
      "/" -> (
        AllRoutes
      )
    ).orNotFound

    val server: ZIO[AllEnv, RuntimeException, ExitCode] = for {
      configuration <- getConfig[Configuration]
      serverConfig = configuration.serverConfig

      async = Async.apply[RIO[AllEnv, *]]
      res <- EmberServerBuilder
        .default(async, Network.forAsync(async))
        .withPort(Port.fromInt(serverConfig.port).get)
        .withHost(Host.fromString(serverConfig.host).get)
        .withHttpApp(allRoutes)
        .build
        .useForever
        .exitCode
    } yield res

    server.unit.provideLayer(layer.all)

  }
}

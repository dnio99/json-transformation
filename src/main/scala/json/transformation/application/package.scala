package json.transformation

import cats.implicits.toSemigroupKOps
import zio.interop.catz._
import json.transformation.layer.AllEnv
import json.transformation.routes.SystemRoutes
import org.http4s.HttpRoutes
import sttp.apispec.openapi.Info
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import zio.RIO

package object application {

  private val swaggerEndpoints = SwaggerInterpreter().fromServerEndpoints(
    SystemRoutes.AllEndpoints,
    Info(
      title = BuildInfo.name,
      version = BuildInfo.version
    )
  )

  private val swaggerRoute: HttpRoutes[RIO[AllEnv, *]] =
    ZHttp4sServerInterpreter()
      .from(swaggerEndpoints)
      .toRoutes

  private[application] val AllRoutes: HttpRoutes[RIO[AllEnv, *]] =
    swaggerRoute <+> SystemRoutes.AllRoutes

}

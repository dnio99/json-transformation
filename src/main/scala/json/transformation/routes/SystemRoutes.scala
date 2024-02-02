package json.transformation.routes

import io.circe.Json
import io.circe.generic.JsonCodec
import json.transformation.graalvm.Js
import json.transformation.layer.AllEnv
import json.transformation.models.errors.DomainErrors
import json.transformation.services.JsonExpression
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import sttp.tapir.ztapir._
import zio.{BuildInfo, ZIO}

object SystemRoutes {

  private val systemEndpoint = endpoint.get
    .in("system")
    .in("version")
    .out(stringBody)
    .summary("获取版本")
    .zServerLogic(_ => systemLogic)

  private val systemLogic = ZIO.succeed(BuildInfo.toString)

  @JsonCodec
  final case class TransformationIn(
      rawData: Json,
      expression: String
  )

  private val transformationEndpoint = ROUTERS.post
    .in("v1")
    .in("transformations")
    .in(jsonBody[TransformationIn])
    .out(jsonBody[Json])
    .summary("json transformation")
    .description("json transformation")
    .tag("转换")
    .zServerLogic(transformationIn => transformationLogic(transformationIn))

  private def transformationLogic(
      transformationIn: TransformationIn
  ): ZIO[Js, DomainErrors.InternalServerError, Json] = {

    for {
      js <- ZIO.service[Js]

      res <- js.transaction(
        JsonExpression(transformationIn.rawData)
          .evaluate(transformationIn.expression)
          .mapError(e =>
            DomainErrors.InternalServerError("转换异常！", Some(e.getMessage))
          )
      )
      _ <- ZIO.logWarning(res.noSpaces)
    } yield res

  }

  val AllEndpoints = List(
    systemEndpoint.widen[AllEnv],
    transformationEndpoint.widen[AllEnv]
  )

  val AllRoutes = ZHttp4sServerInterpreter().from(AllEndpoints).toRoutes
}

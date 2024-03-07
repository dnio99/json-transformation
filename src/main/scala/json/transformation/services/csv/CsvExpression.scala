package json.transformation.services.csv

import io.circe.Json
import io.circe.generic.JsonCodec
import json.transformation.graalvm.JsContext
import json.transformation.services.csv.CsvExpression.CirceOps
import json.transformation.services.json.{JsonExpression, JsonPathValuePair}
import zio.ZIO

/** csv 表达式
  *
  * @param csvHeader
  *   csv头
  * @param jsonExpression
  *   json 表达式
  * @param fieldExpression
  *   字段表达式
  */
@JsonCodec
final case class CsvExpression(
    csvHeader: String,
    jsonExpression: Option[String],
    fieldExpression: String
) {

  def handle(
      value: String
  ): ZIO[JsContext, Throwable, Option[JsonPathValuePair]] = {
    val originJson = Json.fromString(value)
    for {

      json <- jsonExpression
        .map(
          JsonExpression(originJson).evaluate
        )
        .getOrElse(
          ZIO.succeed(originJson)
        )

    } yield
      if (json.isDefine) {
        Some(
          JsonPathValuePair(
            key = fieldExpression,
            value = json
          )
        )
      } else {
        None
      }
  }

}

object CsvExpression {

  implicit class CirceOps(val json: Json) extends AnyVal {

    def isDefine: Boolean = {

      if (json.isNull) {
        false
      } else {
        (
          json.isString,
          json.isArray,
          json.isObject
        ) match {
          case (true, _, _) =>
            json.asString.exists(_.nonEmpty)
          case (_, true, _) => json.asArray.exists(_.nonEmpty)
          case (_, _, true) => json.asObject.exists(_.nonEmpty)
          case _            => true

        }
      }

    }

  }
}

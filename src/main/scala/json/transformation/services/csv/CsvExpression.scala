package json.transformation.services.csv

import io.circe.Json
import io.circe.generic.JsonCodec
import json.transformation.graalvm.JsContext
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
  ): ZIO[JsContext, Throwable, JsonPathValuePair] = {
    val originJson = Json.fromString(value)
    for {

      json <- jsonExpression
        .map(
          JsonExpression(originJson).evaluate
        )
        .getOrElse(
          ZIO.succeed(originJson)
        )

    } yield JsonPathValuePair(
      key = fieldExpression,
      value = json
    )
  }

}

object CsvExpression {}

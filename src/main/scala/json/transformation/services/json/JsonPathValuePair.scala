package json.transformation.services.json

import com.oracle.truffle.js.lang.JavaScriptLanguage.ID
import io.circe.Json
import io.circe.generic.JsonCodec
import io.circe.parser._
import io.circe.syntax.EncoderOps
import json.transformation.graalvm.JsContext
import org.graalvm.polyglot.Source
import zio.ZIO

/** 构建json
  * @param key
  *   Json Path 路径
  * @param value
  *   json 值
  */
@JsonCodec
final case class JsonPathValuePair(
    key: String,
    value: Json
)

object JsonPathValuePairHandle {

  private val source = Source
    .newBuilder(
      ID,
      """(
        |function buildJsonObject(input) {
        |      const keyValuePairs = JSON.parse(input)
        |      let jsonObject = {};
        |      keyValuePairs.forEach(({ key, value }) => {
        |         _.set(jsonObject, key, value);
        |       });
        |      return JSON.stringify(jsonObject);
        |    }
        |)""".stripMargin,
      "build.js"
    )
    .build()

  def apply(
      jsonPathValuePairs: List[JsonPathValuePair]
  ): ZIO[JsContext, Throwable, Json] = {
    for {
      jsContext <- ZIO.service[JsContext]

      context = jsContext.context

      buildJsonObject <- ZIO.attemptBlocking(
        context.eval(
          source
        )
      )

      jsonRes <- ZIO
        .fromEither(
          parse(
            buildJsonObject.execute(jsonPathValuePairs.asJson.noSpaces).toString
          )
        )
        .map(_.deepDropNullValues)

    } yield jsonRes
  }

}

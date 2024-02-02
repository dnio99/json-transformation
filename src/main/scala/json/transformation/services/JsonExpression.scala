package json.transformation.services

import com.oracle.truffle.js.lang.JavaScriptLanguage.ID
import io.circe.Json
import io.circe.parser.parse
import json.transformation.graalvm.JsContext
import zio.ZIO

import java.util.function.Consumer

case class JsonExpression(
    json: Json
) {

  var str: Option[String] = None

  def evaluate(
      expression: String
  ): ZIO[JsContext, Throwable, Json] = {

    for {
      jsContext <- ZIO.service[JsContext]

      context = jsContext.context

      promise <- ZIO
        .attemptBlocking(
          context.eval(
            ID,
            s"""
               | jsonata(`${expression}`).evaluate(${json.noSpaces}).then(
               |      (value) => Promise.resolve(JSON.stringify(value)),
               |      (error) => Promise.reject(error)
               |    )
               |""".stripMargin
          )
        )

      javaThen: Consumer[Object] = (value: Object) => {
        if (value == null) {
          str = None
        } else {
          str = Some(value.toString)
        }
      }

      _ <- ZIO.attemptBlocking(
        promise.invokeMember(
          "then",
          javaThen
        )
      )

      res <- str match {
        case Some(value) => ZIO.fromEither(parse(value))
        case None        => ZIO.succeed(Json.Null)
      }

    } yield res
  }
}

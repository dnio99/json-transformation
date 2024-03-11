package json.transformation.services.csv

import io.circe.Json
import io.circe.syntax._
import io.circe.generic._
import fs2._
import fs2.data.csv._
import fs2.io.file._
import json.transformation.graalvm.{Js, JsContext}
import json.transformation.models.errors.DomainErrors
import json.transformation.services.json.JsonPathValuePairHandle
import zio.{IO, Scope, Task, ZEnvironment, ZIO, ZIOAppArgs, ZIOAppDefault}
import zio.interop.catz.{asyncInstance, asyncRuntimeInstance}
import zio.stream.{ZPipeline, ZStream}
import zio.{Chunk => ZChunk}
import zio.stream.interop.fs2z._

import java.nio.charset.StandardCharsets

object CsvToJson {

  def transfer(
      zStream: ZStream[Any, Throwable, Byte]
  ): ZIO[Js, DomainErrors.DomainError, ZStream[Any, Throwable, Byte]] = {

    val csvStream: Stream[Task, Row] = zStream.toFs2Stream
      .through(
        text.utf8.decode
      )
      .through(lowlevel.rows[Task, String](',', QuoteHandling.RFCCompliant))

    for {

      defaultHeaders <- csvStream
        .take(3)
        .compile
        .toList
        .mapError(e =>
          DomainErrors.InternalServerError(description = Some(e.getMessage))
        )

      csvExpressionList <- (defaultHeaders.map(_.values.toList) match {
        case List(headers, jsonExpressions, filedExpressions) => {
          ZIO succeed (headers zip (jsonExpressions).zip(filedExpressions))
            .map { case (csvHeader, (jsonExpression, fieldExpression)) =>
              CsvExpression(
                csvHeader = csvHeader,
                jsonExpression = Some(jsonExpression).filter(_.nonEmpty),
                fieldExpression = fieldExpression
              )
            }
        }
        case _ => ZIO fail DomainErrors.BadRequest("错误csv to json格式！")
      })
        .tapError(e => ZIO.logWarning(e.toString))

      js <- ZIO.service[Js]
      stream = csvStream
        .toZStream()
        .drop(3)
        .mapZIOPar(32)(row => handleRow(csvExpressionList, row))
        .map(_.noSpaces)
        .intersperse(",")
        .via(
          ZPipeline.utf8Encode
        )
        .provideEnvironment(
          ZEnvironment[Js](js)
        )

    } yield ZStream.fromChunk(
      ZChunk.from("[".getBytes(StandardCharsets.UTF_8))
    ) ++ stream ++ ZStream.fromChunk(
      ZChunk.from("]".getBytes(StandardCharsets.UTF_8))
    )

  }

  def handleRow(
      csvExpressions: List[CsvExpression],
      row: Row
  ): ZIO[Js, Throwable, Json] = {

    val values = row.values.toList

    val io = for {
      jsonPathValuePairs <- ZIO
        .foreach(
          (csvExpressions zip values)
        ) { case (csvExpression, value) =>
          csvExpression.handle(value)
        }
        .map(_.flatten)

      res <- JsonPathValuePairHandle(
        jsonPathValuePairs
      )
    } yield res

    ZIO
      .serviceWithZIO[Js](_.transaction(io))
      .tapError(e => ZIO.logWarning(e.toString))

  }

}

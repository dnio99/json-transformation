package json.transformation.services.csv

import fs2._
import fs2.data.csv._
import fs2.io.file._
import zio.{Scope, Task, ZIO, ZIOAppArgs, ZIOAppDefault}
import zio.interop.catz.{asyncInstance, asyncRuntimeInstance}
import zio.stream.ZStream
import zio.stream.interop.fs2z._

object CsvToJson {

  def transfer(
      zStream: ZStream[Any, Throwable, Byte]
  ) = {

    zStream.toFs2Stream
      .through(
        text.utf8.decode
      )
      .through(lowlevel.rows[Task, String](',', QuoteHandling.Literal))
      .map(_.values.toList.mkString(","))
      .through(text.utf8.encode)
      .toZStream()

  }

}

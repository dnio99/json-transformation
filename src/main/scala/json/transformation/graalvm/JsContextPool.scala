package json.transformation.graalvm

import com.oracle.truffle.js.lang.JavaScriptLanguage.ID
import json.transformation.Configuration
import org.graalvm.polyglot.{Context, HostAccess, PolyglotAccess, Source}
import zio.stream.ZStream
import zio.{Ref, ZIO, ZLayer}

import java.nio.charset.StandardCharsets

final case class JsContext(id: String, context: Context)

object JsContext {

  private def loadJsFile(
      filename: String,
      context: Context
  ): ZIO[Any, Throwable, Unit] = {

    for {
      bytes <- ZStream
        .fromZIO(
          for {
            inputStreamOpt <- ZIO
              .attemptBlocking(
                ClassLoader.getSystemResourceAsStream(filename)
              )
              .map(Option(_))
            inputStream <- ZIO
              .fromOption(inputStreamOpt)
              .orElseFail(new RuntimeException(s"文件${filename}不存在！"))
          } yield inputStream
        )
        .flatMap(
          ZStream.fromInputStream(_)
        )
        .runCollect

      jsonataFile <- ZIO.attempt(
        new String(bytes.toArray, StandardCharsets.UTF_8)
      )

      source <- ZIO.attempt(
        Source
          .newBuilder(
            ID,
            jsonataFile,
            filename
          )
          .build()
      )
      _ <- ZIO.attempt(context.eval(source))
      _ <- ZIO.logDebug(s"load js file: ${filename} success!")

    } yield ()
  }

  val created: ZIO[Any, Throwable, Context] = for {
    jsContext <- ZIO.attempt(
      Context
        .newBuilder(ID)
        .allowHostAccess(HostAccess.ALL)
        .allowPolyglotAccess(PolyglotAccess.ALL)
        .allowHostClassLookup(_ => true)
        .build()
    )

    _ <- loadJsFile("jsonata.min.js", jsContext)

  } yield jsContext

}

/** js context 连接池
  * @param contexts
  *   contexts
  */
final case class JsContextPool(
    contexts: Ref[Vector[JsContext]]
) {

  /** 获取js context
    * @return
    *   js context
    */
  def obtain: ZIO[Any, Nothing, JsContext] = {
    contexts
      .modify {
        case h +: t => (h, t)
        case _ => throw new IllegalStateException("No Js context available")
      }
      .tap(jsContext => ZIO.logDebug(s"Obtained js context: ${jsContext.id}"))
  }

  /** 释放这个js context
    * @param jsContext
    *   js context
    * @return
    */
  def release(
      jsContext: JsContext
  ): ZIO[Any, Nothing, Unit] = contexts
    .modify(jcs => {
      ((), jcs :+ jsContext)
    })
    .zipLeft(ZIO.logDebug(s"Released js context: ${jsContext.id}"))
}

object JsContextPool {

  val live: ZLayer[Configuration, Throwable, JsContextPool] = {
    ZLayer {
      for {
        jsContextConfig <- ZIO.serviceWith[Configuration](_.jsContextConfig)

        poolSize = jsContextConfig
          .flatMap(_.poolSize)
          .filter(_ >= 1)
          .getOrElse(10)

        ids = Range
          .apply(0, poolSize)
          .map(n => s"js-context-pool-${n}")

        jsContexts <- ZIO.foreachPar(
          ids
        )(id =>
          for {
            context <- JsContext.created

            _ <- ZIO.logInfo(s"init js context id:${id} success!")

          } yield JsContext(id = id, context = context)
        ).withParallelism(64)

        res <- Ref.make(
          jsContexts.toVector
        )
      } yield JsContextPool(res)
    }
  }

}

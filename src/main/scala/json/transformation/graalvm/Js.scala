package json.transformation.graalvm

import zio.{Scope, ZIO, ZLayer}

case class Js(jsContextPool: JsContextPool) {

  private def connection: ZIO[Scope, Nothing, JsContext] = {
    for {
      res <- ZIO.acquireRelease(
        jsContextPool.obtain
      )(jsContext => jsContextPool.release(jsContext))
    } yield res
  }

  def transaction[R, E, A](
      program: ZIO[JsContext with R, E, A]
  ): ZIO[R, E, A] = {
    ZIO.scoped[R] {
      connection.flatMap { context =>
        program.provideSomeLayer[R](
          ZLayer.succeed(context)
        )
      }
    }
  }

}

object Js {
  val live: ZLayer[JsContextPool, Nothing, Js] = ZLayer.fromFunction(
    Js(_)
  )
}

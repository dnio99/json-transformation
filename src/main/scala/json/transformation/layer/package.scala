package json.transformation

import json.transformation.graalvm.{Js, JsContextPool}
import zio.ZLayer

package object layer {

  type AllEnv = Configuration with JsContextPool with Js

  val all = ZLayer.make[AllEnv](
    Configuration.live,
    JsContextPool.live,
    Js.live,
    LoggingLayer.live
  )
}

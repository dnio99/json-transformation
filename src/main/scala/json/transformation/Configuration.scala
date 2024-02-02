package json.transformation

import zio.ZLayer
import zio.config._
import zio.config.magnolia.descriptor
import zio.config.typesafe.TypesafeConfig

object Configuration {

  val live: ZLayer[Any, Nothing, Configuration] = {
    val configurationDescriptor: ConfigDescriptor[Configuration] =
      descriptor[Configuration]

    val configurationLayer: ZLayer[Any, Nothing, Configuration] =
      TypesafeConfig.fromResourcePath(configurationDescriptor).orDie

    ZLayer.make[Configuration](
      configurationLayer
    )
  }

}
final case class ServerConfig(host: String, port: Int)

/** js context 配置
  * @param poolSize
  *   池数量
  */
final case class JsContextConfig(
    poolSize: Option[Int]
)

final case class Configuration(
    serverConfig: ServerConfig,
    jsContextConfig: Option[JsContextConfig]
)

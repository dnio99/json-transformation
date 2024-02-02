package json.transformation.models.errors

import io.circe.generic.JsonCodec

object DomainErrors {

  sealed trait DomainError {
    val message: String
    val description: Option[String]
  }

  /** 400
    *
    * 请求语法错误、无效请求消息格式或者欺骗性请求路由
    * @param message
    *   错误信息
    * @param description
    *   具体描述
    */
  @JsonCodec
  final case class BadRequest(
      message: String = "BadRequest!",
      description: Option[String] = None
  ) extends DomainError

  /** 401
    *
    * @param message
    *   错误信息
    * @param description
    *   具体描述
    */
  @JsonCodec
  final case class Unauthorized(
      message: String = "Unauthorized!",
      description: Option[String] = None
  ) extends DomainError

  /** 403
    *
    * @param message
    *   错误信息
    * @param description
    *   具体描述
    */
  @JsonCodec
  final case class Forbidden(
      message: String = "Internal Server Error!",
      description: Option[String] = None
  ) extends DomainError

  /** 404
    *
    * @param message
    *   错误信息
    * @param description
    *   具体描述
    */
  @JsonCodec
  final case class NotFound(
      message: String = "Not Found!",
      description: Option[String] = None
  ) extends DomainError

  /** 409
    *
    * @param message
    *   错误信息
    * @param description
    *   具体描述
    */
  @JsonCodec
  final case class Conflict(
      message: String = "Conflict",
      description: Option[String] = None
  ) extends DomainError

  @JsonCodec
  final case class NotAcceptable(
      message: String = "Not Acceptable",
      description: Option[String] = None
  ) extends DomainError

  /** 423
    *
    * @param message
    *   错误信息
    * @param description
    *   具体描述
    */
  @JsonCodec
  final case class Locked(
      message: String = "Locked!",
      description: Option[String] = None
  ) extends DomainError

  /** 428
    *
    * @param message
    *   错误信息
    * @param description
    *   具体描述
    */
  @JsonCodec
  final case class PreconditionRequired(
      message: String = "Precondition Required!",
      description: Option[String] = None
  ) extends DomainError

  /** 429
    *
    * @param message
    *   错误信息
    * @param description
    *   具体描述
    */
  @JsonCodec
  final case class TooManyRequests(
      message: String = "Too Many Requests!",
      description: Option[String] = None
  ) extends DomainError

  /** 500
    *
    * @param message
    *   错误信息
    * @param description
    *   具体描述
    */
  @JsonCodec
  final case class InternalServerError(
      message: String = "Internal Server Error!",
      description: Option[String] = None
  ) extends DomainError

}

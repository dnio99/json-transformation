package json.transformation

import json.transformation.models.errors.DomainErrors._
import sttp.model.StatusCode
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.circe._
import sttp.tapir.ztapir._
import sttp.tapir.{Endpoint, EndpointOutput}

package object routes {

  private val errorInfoEndpointOutput
      : EndpointOutput.OneOf[DomainError, DomainError] = oneOf[DomainError](
    oneOfVariant(
      StatusCode.NotFound,
      jsonBody[NotFound].description("NotFound")
    ),
    oneOfVariant(
      StatusCode.BadRequest,
      jsonBody[BadRequest].description("BadRequest")
    ),
    oneOfVariant(
      StatusCode.InternalServerError,
      jsonBody[InternalServerError].description("internal server error")
    ),
    oneOfVariant(
      StatusCode.Forbidden,
      jsonBody[Forbidden].description("Forbidden")
    ),
    oneOfVariant(
      StatusCode.PreconditionRequired,
      jsonBody[PreconditionRequired].description("Precondition Required")
    ),
    oneOfVariant(
      StatusCode.TooManyRequests,
      jsonBody[TooManyRequests].description("rate limit exceeded")
    ),
    oneOfVariant(
      StatusCode.Conflict,
      jsonBody[Conflict].description("Conflict")
    ),
    oneOfVariant(
      StatusCode.NotAcceptable,
      jsonBody[NotAcceptable].description("NotAcceptable")
    ),
    oneOfVariant(
      StatusCode.NotFound,
      jsonBody[NotFound].description("NotFound")
    ),
    oneOfVariant(
      StatusCode.Unauthorized,
      jsonBody[Unauthorized].description("Unauthorized")
    ),
    oneOfVariant(
      StatusCode.Locked,
      jsonBody[Locked].description("Locked")
    )
  )

  protected[routes] val ROUTERS: Endpoint[Unit, Unit, DomainError, Unit, Any] =
    endpoint
      .errorOut(
        errorInfoEndpointOutput
      )

}

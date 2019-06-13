import akka.http.scaladsl.model.{StatusCodes, StatusCode}

final case class ApiError private(statusCode: StatusCode, message: String)

object ApiError {

  def apply(statusCode: StatusCode, message: String) = new ApiError(statusCode, message)

  val generic = new ApiError(StatusCodes.InternalServerError, "Unknown error.")
}
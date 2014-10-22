package filters

import controllers.Default
import play.api.mvc.{SimpleResult, RequestHeader, Filter}

/**
 * Author: Dennis Fricke
 * Date: 22.10.2014
 */
case class CORSFilter() extends Filter{
	import scala.concurrent._
	import ExecutionContext.Implicits.global
	lazy val allowedDomain = play.api.Play.current.configuration.getString("cors.allowed.domain")
	def isPreFlight(r: RequestHeader) =(
		r.method.toLowerCase.equals("options")
			&&
			r.headers.get("Access-Control-Request-Method").nonEmpty
		)

	def apply(f: (RequestHeader) => Future[SimpleResult])(request: RequestHeader): Future[SimpleResult] = {
		if (isPreFlight(request)) {
			Future.successful(Default.Ok.withHeaders(
				"Access-Control-Allow-Origin" -> allowedDomain.orElse(request.headers.get("Origin")).getOrElse(""),
				"Access-Control-Allow-Methods" -> request.headers.get("Access-Control-Request-Method").getOrElse("*"),
				"Access-Control-Allow-Headers" -> request.headers.get("Access-Control-Request-Headers").getOrElse(""),
				"Access-Control-Allow-Credentials" -> "true"
			))
		} else {
			f(request).map{_.withHeaders(
				"Access-Control-Allow-Origin" -> allowedDomain.orElse(request.headers.get("Origin")).getOrElse(""),
				"Access-Control-Allow-Methods" -> request.headers.get("Access-Control-Request-Method").getOrElse("*"),
				"Access-Control-Allow-Headers" -> request.headers.get("Access-Control-Request-Headers").getOrElse(""),
				"Access-Control-Allow-Credentials" -> "true"
			)}
		}
	}
}

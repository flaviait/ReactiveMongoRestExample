import filters.CORSFilter
import play.api.{GlobalSettings}
import play.api.mvc._

/**
 * Author: Dennis Fricke
 * Date: 22.10.2014
 */
object Global extends WithFilters(CORSFilter()) with GlobalSettings {

}
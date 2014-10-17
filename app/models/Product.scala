package models

import org.joda.time.DateTime
import reactivemongo.bson.BSONObjectID

/**
 * Author: Dennis Fricke
 * Date: 23.09.2014
 */
case class Product(_id: Option[BSONObjectID],
								 description: String,
								 price: Double,
								 comments: List[Option[Comment]],
								 created: Option[DateTime])

object Product {
	import play.api.libs.json._
	import play.modules.reactivemongo.json.BSONFormats._

	implicit val dateTimeReads = Reads.jodaDateReads("yyyy-MM-dd HH:mm:ss")
	implicit val dateTimeWrites = Writes.jodaDateWrites("yyyy-MM-dd HH:mm:ss")

	implicit val productReads = Json.reads[Product]
	implicit val productWrites = Json.writes[Product]
}

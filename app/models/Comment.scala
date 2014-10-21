package models

import reactivemongo.bson.BSONObjectID

/**
 * Author: Dennis Fricke
 * Date: 06.10.2014
 */
case class Comment(username: String,
								 comment: String)

object Comment {
	import play.api.libs.json._
	import play.modules.reactivemongo.json.BSONFormats._

	implicit val commentReads = Json.reads[Comment]
	implicit val commentWrites = Json.writes[Comment]
}
package controllers

import play.api.mvc._
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.Future
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import models.Product

/**
 * Author: Dennis Fricke
 * Date: 23.09.2014
 */
object Product extends Controller with MongoController {
	def collection = db.collection[JSONCollection]("products")

	def post = Action.async(parse.json) { request =>
		request.body.validate[Product].map {
			case product => {
				val futureResult = collection.insert(product)
				futureResult.map {
					case t => t.inError match {
						case true => InternalServerError("%s".format(t))
						case false => Ok(Json.toJson(product))
					}
				}
			}
		}.recoverTotal{
			e => Future { BadRequest(JsError.toFlatJson(e)) }
		}
	}

	def find(id: String) = Action.async(parse.anyContent) { request =>
		val cursor = collection.find(Json.obj("_id" -> Json.obj("$oid" ->id))).cursor[Product]
		val futureResults = cursor.collect[List]()
		futureResults.map {
			case t => Ok(Json.toJson(t))
		}
	}

}

package controllers

import models.Product
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import play.api.mvc._
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection

import scala.concurrent.Future

/**
 * Author: Dennis Fricke
 * Date: 23.09.2014
 */
object Product extends Controller with MongoController {
	def collection = db.collection[JSONCollection]("products")

	/**
	 * Create a new product
	 * @return
	 */
	def post = Action.async(parse.json) { request =>
		request.body.validate[Product].map {
			case product => {
				val futureResult = collection.save(product)
				futureResult.map {
					case t => t.inError match {
						case true => InternalServerError("%s".format(t))
						case false => Ok(Json.toJson(product))
					}
				}
			}
		}.recoverTotal {
			e => Future {
				BadRequest(JsError.toFlatJson(e))
			}
		}
	}

	/**
	 * List all products
	 * @return
	 */
	def list() = Action.async { request =>
		val cursor = collection.find(Json.obj()).cursor[Product]
		val futureResults = cursor.collect[List]()
		futureResults.map {
			case t => Ok(Json.toJson(t))
		}
	}

	/**
	 * Get a product by id
	 * @param id
	 * @return
	 */
	def find(id: String) = Action.async(parse.anyContent) { request =>
		val futureResults: Future[Option[Product]] = collection.find(Json.obj("_id" -> Json.obj("$oid" -> id))).one[Product]
		futureResults.map {
			case t => Ok(Json.toJson(t))
		}
	}

	/**
	 * Update a product by id
	 * @param id
	 * @return
	 */
	def update(id: String) = Action.async(parse.json) { request =>
		request.body.validate[Product].map {
			case product => {
				val futureResult = collection.update(Json.obj("_id" -> Json.obj("$oid" -> id)), product)
				futureResult.map {
					case t => t.inError match {
						case true => InternalServerError("%s".format(t))
						case false => Ok(Json.toJson(product))
					}
				}
			}
		}.recoverTotal {
			e => Future {
				BadRequest(JsError.toFlatJson(e))
			}
		}
	}

	/**
	 * Delete a product by id
	 * @param id
	 * @return
	 */
	def delete(id: String) = Action.async(parse.anyContent) { request =>
		val futureResult = collection.remove(Json.obj("_id" -> Json.obj("$oid" -> id)), firstMatchOnly = true)
		futureResult.map {
			case t => t.inError match {
				case true => InternalServerError("%s".format(t))
				case false => Ok("success")
			}
		}
	}

}

package controllers

import db.CassandraConnection
import play.api.libs.json.Json
import javax.inject._
import play.api.mvc._

@Singleton
class PersonController @Inject() (cc: ControllerComponents) extends AbstractController(cc) {

  def all: Action[AnyContent] = Action {
    Ok(Json.toJson(CassandraConnection.getPersons))
  }


}

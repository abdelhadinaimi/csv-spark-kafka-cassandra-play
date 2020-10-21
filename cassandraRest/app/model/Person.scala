package model

import play.api.libs.functional.syntax.{toApplicativeOps, toFunctionalBuilderOps}
import play.api.libs.json.Reads._
import play.api.libs.json.{Json, Reads, Writes, __}

case class Person(email: String, firstname: String, lastname: String /*, gender: Genders.Value*/)

object Person {
  implicit val writes: Writes[Person] = (o: Person) => Json.obj(
    "email" -> o.email,
    "first_name" -> o.firstname, // same as the others
    "last_name" -> o.lastname,
  )

  implicit val reads : Reads[Person] = (
    (__ \ "email").read[String](maxLength[String](256) ~> minLength[String](2) ~> email) ~
      (__ \ "first_name").read[String](maxLength[String](63) ~> minLength[String](2)) ~
        (__ \ "last_name").read[String]
    )(Person.apply _) // apply is the constructor function
}
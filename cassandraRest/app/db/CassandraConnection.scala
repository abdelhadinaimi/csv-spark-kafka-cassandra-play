package db

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.{ResultSet, Row, SimpleStatement}
import com.datastax.oss.driver.api.core.paging.OffsetPager
import model.Person

object CassandraConnection {
  val table = "person_by_email"
  val session: CqlSession = CqlSession.builder.build
  val offsetPage = new OffsetPager(20)

  def getPersons: Array[Person] = {
    try {

      val rs = session.execute(
        SimpleStatement.builder("SELECT * FROM person_by_email")
          .build())
      rs.all()
        .toArray(Array[Row]())
        .map(row => new Person(row.getString("email"), row.getString("first_name"), row.getString("last_name")))
    } catch {
      case e: Exception => Array[Person]()
    }
  }

}

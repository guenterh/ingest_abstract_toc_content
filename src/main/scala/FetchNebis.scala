import java.net.URL
import java.util.Date

import db.ctx
import io.getquill._
import io.getquill.context.jdbc.JdbcContext


case class Content(id:Long, docid: String, content: String, url:String, date: Date)

import io.getquill.{Literal, MySQLDialect}

//https://stackoverflow.com/questions/39094836/scala-quill-quote-not-resolving

package object db {
  //com.zaxxer.hikari.HikariConfig

  //type JdbcDatabase = JdbcContext[MySQLDialect, Literal]

  //lazy val ctx = new JdbcDatabase
  lazy val ctx = new MysqlJdbcContext(SnakeCase, "ctx1")
  //type JdbcDatabase = JdbcContext[MySQLDialect, Literal]
  //lazy val ctx = new JdbcDatabase("ctx")
}

//https://blog.vogonjeltz.com/scala/programming/open-source/mysql/2016/08/19/actually-getting-data-with-quill.html
//import db._

object Main extends App {

  dbAccessWrapper.hasPdf("http://opac.nebis.ch/objects/pdf/e65_3-7170-0201-5_01.pdf")

}

object dbAccessWrapper {
  import ctx._

  def hasPdf(url: String ): Boolean = {

    val qu: ctx.Quoted[ctx.EntityQuery[Content]] = quote {

      //query[Content].filter(_.url == s"\'$url\'")
      query[Content].filter(_.url == lift(url))
    }
    ctx.run(qu).nonEmpty
  }

}

class ProtocolTest (val url: String) {

  private val protocol = new URL(url).getProtocol

  def relevantProtocol: Boolean = protocol.equalsIgnoreCase("http") ||
    protocol.equalsIgnoreCase("https")

}



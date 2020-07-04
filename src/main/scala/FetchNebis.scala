import java.io.{File, FileInputStream, InputStream}
import java.net.URL
import java.nio.file.{Files, Paths}
import java.util.Date
import java.util.stream.Collectors
import java.util.zip.GZIPInputStream

import db.ctx
import io.getquill._
import io.getquill.context.jdbc.JdbcContext
import utilities.{MarcXMLHandlersFeatures, Transformators}

import scala.collection.immutable
import scala.io.Source



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

  fileList.getFileList.forEach( f => {

    val source = fileList.getStream(f)
    sourceParser.parseSource(source)

  }

  )

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

object fileList {

  def getFileList: java.util.List[String] = {

     Files.walk(Paths.get("/swissbib_index/solrDocumentProcessing/FrequentInitialPreProcessing/data/format")).
      filter(Files.isRegularFile(_)).map[String](_.toString).collect(Collectors.toList[String])

  }

  def getStream(fileName: String) : InputStream = {
    val infile = new File(fileName)

    val nameInFile: String =  infile.getAbsoluteFile.getName
    val zipped = if (nameInFile.matches(""".*?.gz$""")) true else false
    val  source: InputStream = if (zipped) {
      new GZIPInputStream(new FileInputStream(infile))
    } else {
      new FileInputStream(infile)
    }

    source
  }

}


object sourceParser extends Transformators
          with MarcXMLHandlersFeatures {


  def parseSource(stream: InputStream): Unit = {

    val it = Source.fromInputStream(stream).getLines()
    for (line <- it if isRecord(line)) {
      val elem = parseRecord(line)
      val urls =   (getRField(elem)("856").map(getRSubfieldContent(_)("u")) ++
        (getRField(elem)("956").map(getRSubfieldContent(_)("u")))).flatten.
        map(_.text)
      if (urls.nonEmpty) {
        println(urls)
      }




    }


  }

}



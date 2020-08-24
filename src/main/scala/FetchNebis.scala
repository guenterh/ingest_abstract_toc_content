import java.io.{ByteArrayInputStream, File, FileInputStream, InputStream}
import java.net.URL
import java.nio.file.{Files, Paths}
import java.util.Date
import java.util.stream.Collectors
import java.util.zip.GZIPInputStream

import db.ctx
import io.getquill._
import io.getquill.context.jdbc.JdbcContext
import org.apache.tika.Tika
import org.joda.time.DateTime
import utilities.OptionsParser.OptionMap
import utilities.{MarcXMLHandlersFeatures, OptionsParser, Transformators}

import scala.io.Source
import scala.reflect.runtime.universe.Try



case class Content(id:Option[Long], docid: Option[String], content: String, url:String, date: Date)

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

  val options: OptionMap = OptionsParser.nextOption(Map(), args.toList)
  assert(options.contains(Symbol("indir")))

  private object FileList {

    def getFileList: java.util.List[String] = {
      Files.walk(Paths.get(options(Symbol("indir")).toString)).
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

  FileList.getFileList.forEach( f => {

    val source = FileList.getStream(f)
    SourceParser.parseSource(source)
  })
}


class ProtocolTest (val url: String) {

  private val protocol = new URL(url).getProtocol
  def relevantProtocol: Boolean = protocol.equalsIgnoreCase("http") ||
    protocol.equalsIgnoreCase("https")
}



object SourceParser extends Transformators
          with MarcXMLHandlersFeatures {


  private[this] val tika = {
    val tika = new Tika()
    tika.setMaxStringLength(30000)
    tika
  }


  private val nebisRegex = "http://opac\\.nebis\\.ch/objects/pdf/.*?\\.pdf|https://opac\\.nebis\\.ch/objects/pdf/.*?\\.pdf".r

  def parseSource(stream: InputStream): Unit = {


    def replace2https (url: String) = url.replace("http","https")
    def replace2http (url: String) = url.replace("https","http")

    val prot_http = "^http://.*".r
    val prot_https = "^https://.*".r


    val it = Source.fromInputStream(stream).getLines()
    for (line <- it if isRecord(line)) {
      val elem = parseRecord(line)
      val urls =   (getRField(elem)("856").map(getRSubfieldContent(_)("u")) ++
        (getRField(elem)("956").map(getRSubfieldContent(_)("u")))).flatten.
        map(_.text)

      urls.filter(nebisRegex.findFirstIn(_).nonEmpty).
        filter(url =>
          {
            url match {
              case prot_http(_) => !dbAccessWrapper.hasPdf(url) &&
                                !dbAccessWrapper.hasPdf(replace2https(url))
              case prot_https(_) => !dbAccessWrapper.hasPdf(url) &&
                                !dbAccessWrapper.hasPdf(replace2http(url))

              case _ => false //should not happen

            }

          }
        ).map(
          url => (url,fetchContent(url))).
        foreach(insertIntoDb)

    }
  }

  def fetchContent(url:String): String = {
    val requester =  requests.get(url)
    val content = tika.parseToString(new ByteArrayInputStream(requester.bytes))
    content
  }

  def insertIntoDb(urlAndContent:Tuple2[String, String]): Unit = {
    dbAccessWrapper.insertDoc(Content(
      id = Option.empty[Long],
      docid = Option.empty[String],
      content = urlAndContent._2,
      url = urlAndContent._1,
      date = new Date()))
    Thread.sleep(30000)

  }
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

  def insertDoc(contentType: Content) = {
    ctx.run(query[Content].insert(lift(contentType)))
  }
}


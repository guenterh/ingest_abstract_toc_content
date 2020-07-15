import java.util

import org.scalatest.FlatSpec
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor

import scala.collection.mutable
import scala.io.Source

class TestFetchContent extends FlatSpec {

  //in statt ignore zum Aktivieren
  "this document " should "be fetchable " ignore  {
    import db.ctx._
    assert(  dbAccessWrapper.hasPdf("http://opac.nebis.ch/objects/pdf/e65_3-7170-0201-5_01.pdf"))

  }

  "this document " should "not be fetchable " ignore {
    import db.ctx._
    assert(  ! dbAccessWrapper.hasPdf("http://opac.nebis.ch/objects/pdf/e65_3-7170-0201-5_01.pd"))

  }

  "read yaml config" should "be available" in {

    val map: mutable.LinkedHashMap[String,mutable.LinkedHashMap[String,util.ArrayList[String]]] = null

    //val map: mutable.LinkedHashMap[String,util.ArrayList[String]] = null
    val config = Source.fromResource("config.yaml").bufferedReader()
    val yaml = new Yaml(new Constructor(classOf[mutable.LinkedHashMap[String,util.ArrayList[String]]]))
    //val e = yaml.load(config).asInstanceOf[mutable.LinkedHashMap[String,util.ArrayList[String]]]

    yaml.load(config)

    //val config = Source.fromResource("config.yaml").bufferedReader()
    val yaml1 = new Yaml()
    val loaded = yaml1.loadAll(config)
    loaded.forEach(item => {
      println(item)
    })
    println(loaded)

  }



}

package utilities

import scala.collection.immutable
import scala.xml.Elem

trait MarcXMLHandlersFeatures extends MarcXmlHandlers {

  private val isAcceptedSourceId = """^\(.*?\)""".r

  def isbnFeatures(elem: Elem): immutable.Seq[String] = {

    (getRField(elem)("020").map(getRSubfieldContent(_)("a")) ++
      (getRField(elem)("022").map(getRSubfieldContent(_)("a")))).flatten.
      map(_.text)

  }

  def ttl245Feature(elem: Elem): immutable.Map[String,Seq[String]] = {

      Map("245"->
      (((getRField(elem)("245").map(getRSubfieldContent(_)("a")) ++
        (getRField(elem)("245").map(getRSubfieldContent(_)("b")))) ++
        (if ((getRSubfieldContent(getNRField(elem)("245"))("p")).nonEmpty)
          (getRField(elem)("245").map(getRSubfieldContent(_)("p")))
        else
          (getRField(elem)("245").map(getRSubfieldContent(_)("n"))))).flatten.
        map(_.text)))

  }

  def ttl246Feature(elem: Elem): immutable.Map[String,Seq[String]] = {

    if (getRField(elem)("246").nonEmpty)
      Map("246" -> (((getRField(elem)("246").map(getRSubfieldContent(_)("a")) ++
        (getRField(elem)("246").map(getRSubfieldContent(_)("b")))) ++
        (if ((getRSubfieldContent(getNRField(elem)("246"))("p")).nonEmpty)
          (getRField(elem)("246").map(getRSubfieldContent(_)("p")))
        else
          (getRField(elem)("246").map(getRSubfieldContent(_)("n"))))).flatten.
        map(_.text)))
    else Map.empty

  }


  def ttlFullFeature(elem: Elem): immutable.Map[String,Seq[String]] = {

      ttl245Feature(elem) ++ ttl246Feature(elem)

  }

  def personFeature(elem: Elem): Map[String, immutable.Seq[String]] = {

    Map("100" -> (for (elem <- (getRField(elem)("100")))
      yield elem.\\("subfield").filter(a =>  List("a","D", "q").contains(a \@ "code")).map(_.text).mkString("")),
      "700" -> (for (elem <- (getRField(elem)("700")))
        yield elem.\\("subfield").filter(a =>  List("a","D", "q").contains(a \@ "code")).map(_.text).mkString("")),
      "800" -> (for (elem <- (getRField(elem)("800")))
        yield elem.\\("subfield").filter(a =>  List("a","c","q").contains(a \@ "code")).map(_.text).mkString("")),
      "245c" -> (for (elem <- (getRField(elem)("245")))
        yield elem.\\("subfield").filter(a =>  List("c").contains(a \@ "code")).map(_.text).mkString(""))
    )
  }


  def all100(elem: Elem): immutable.Seq[String] = {
    //todo: make it better - should be able to collect all subfields at once
    // then it would be a general function
    if (getRField(elem)("100").nonEmpty)
      ((getRField(elem)("100").map(getRSubfieldContent(_)("a")) ++
        (getRField(elem)("100").map(getRSubfieldContent(_)("b"))) ++
        (getRField(elem)("100").map(getRSubfieldContent(_)("c"))))).flatten.
        map(_.text)
    else Nil

  }

  def all700(elem: Elem): immutable.Seq[String] = {
    //todo: make it better - should be able to collect all subfields at once
    // then it would be a general function
    if (getRField(elem)("700").nonEmpty)
      (getRField(elem)("700").map(getRSubfieldContent(_)("a")) ++
        (getRField(elem)("700").map(getRSubfieldContent(_)("b"))) ++
        (getRField(elem)("700").map(getRSubfieldContent(_)("c")))).flatten.
        map(_.text)
    else Nil

  }

  def all100Generic(elem: Elem): immutable.Seq[String] = {
    //todo: make it better - should be able to collect all subfields at once
    // then it would be a general function
    if (getRField(elem)("100").nonEmpty)
      (getRField(elem)("100").map(getAllSubfieldContent(_)).flatten.
        map(_.text))
    else Nil

  }

  def allGeneric(elem: Elem)( datafield:String): immutable.Seq[String] = {
    //todo: make it better - should be able to collect all subfields at once
    // then it would be a general function
    if (getRField(elem)(datafield).nonEmpty)
      (getRField(elem)(datafield).map(getAllSubfieldContent(_)).
        map(_.text))
    else Nil

  }

  def corporateFeature(elem: Elem): Map[String, immutable.Seq[String]] = {

    Map("110" -> (for (elem <- (getRField(elem)("110")))
      yield elem.\\("subfield").filter(a =>  List("a","b", "c").contains(a \@ "code")).map(_.text).mkString("")),
      "710" -> (for (elem <- (getRField(elem)("710")))
        yield elem.\\("subfield").filter(a =>  List("a","b", "c").contains(a \@ "code")).map(_.text).mkString("")),
      "810" -> (for (elem <- (getRField(elem)("810")))
        yield elem.\\("subfield").filter(a =>  List("a","b", "c").contains(a \@ "code")).map(_.text).mkString("")),
    )

  }

  def all35(elem: Elem): immutable.Seq[String] = {
    //todo: make it better - should be able to collect all subfields at once
    // then it would be a general function
      (getRField(elem)("035").map(getRSubfieldContent(_)("a")) .flatten.
        map(_.text))

  }


  def allRelevantSlave35(elem: Elem): immutable.Seq[String] = {
    //todo: make it better - should be able to collect all subfields at once
    // then it would be a general function


    (getRField(elem)("035").map(getRSubfieldContent(_)("a")) .flatten.
      filter(value => !value.text.contains("(OCoLC)") &&
        isAcceptedSourceId.findFirstIn(value.text).isDefined &&
        !value.text.contains("Sz"))
      .map(_.text))

  }

  def firstRelevantSlave35(elem: Elem): Option[String] = {
    //todo: make it better - should be able to collect all subfields at once
    // then it would be a general function

    val isSourceId = """^\(.*?\)""".r
    (getRField(elem)("035").map(getRSubfieldContent(_)("a")) .flatten.
      filter(value => !value.text.contains("(OCoLC)") &&  isSourceId.findFirstIn(value.text).isDefined)
      .map(_.text)) match  {
      case h :: _ => Some(h)
      case _ => None
    }

  }

  def all800(elem: Elem): immutable.Seq[String] = {
    //todo: make it better - should be able to collect all subfields at once
    // then it would be a general function
    if (getRField(elem)("800").nonEmpty)
      ((getRField(elem)("800").map(getRSubfieldContent(_)("a")) ++
        (getRField(elem)("800").map(getRSubfieldContent(_)("b"))) ++
        (getRField(elem)("800").map(getRSubfieldContent(_)("c"))))).flatten.
        map(_.text)
    else Nil

  }

  def recordid(elem: Elem) (): String = {

    getNRControlfieldField(elem)("001").text

  }


  def partOf008Feature(elem: Elem) (start:Int, end: Int): String = {

    val text008 = getNRControlfieldField(elem)("008").text
    text008.slice(start,end + 1)
  }

  def editionFeature(elem: Elem): immutable.Seq[String] = {
    //todo: make it better - should be able to collect all subfields at once
    // then it would be a general function
    if (getRField(elem)("250").nonEmpty)
      (getRField(elem)("250").map(getRSubfieldContent(_)("a"))).flatten.
        map(_.text)
    else Nil
  }

  def partFeature(elem: Elem): immutable.Seq[String] = {

    ((getRField(elem)("773").map(getRSubfieldContent(_)("g"))) ++
      (getRField(elem)("490").map(getRSubfieldContent(_)("v"))) ++
      (getRField(elem)("830").map(getRSubfieldContent(_)("v"))) ++
      (getRField(elem)("440").map(getRSubfieldContent(_)("v"))) ++
      (getRField(elem)("245").map(getRSubfieldContent(_)("n")))).flatten.
      map(_.text)

  }
  def _300a(elem: Elem): immutable.Seq[String] = {
    //todo: make it better - should be able to collect all subfields at once
    // then it would be a general function
    if (getRField(elem)("300").nonEmpty)
      (getRField(elem)("300").map(getRSubfieldContent(_)("a"))).flatten.
        map(_.text)
    else Nil
  }

  def pagesFeature(elem: Elem): immutable.Seq[String] = {

    _300a(elem)
  }

  def volumesFeature(elem: Elem): immutable.Seq[String] = {

    _300a(elem)
  }

  def pubFeature(elem: Elem): immutable.Seq[String] = {

    (getRField(elem)("260").map(getRSubfieldContent(_)("b"))).flatten.map(_.text)

  }

  def scaleFeature(elem: Elem): immutable.Seq[String] = {

    (getRField(elem)("034").map(getRSubfieldContent(_)("b")) match {

      case Nil =>
        println(elem)
        (getRField(elem)("255").map(getRSubfieldContent(_)
                                    ("a")).map(_.text))

        //we have found something for 034$b
      case seq =>
        println(elem)
        seq.map(_.text)

    })

  }

  def coordinateFeature(elem: Elem): immutable.Seq[String] = {

    ((getRField(elem)("034").map(getRSubfieldContent(_)("d"))) ++
      (getRField(elem)("034").map(getRSubfieldContent(_)("f")))).flatten.
      map(_.text)

  }

  def _24a(elem: Elem): immutable.Seq[String] = {

    getRField(elem)("024").filter(p => (p \\ "subfield").exists(sf => sf \@ "code" == "2")).
      map(getRSubfieldContent(_)("a")).flatten.map(_.text)
    //val nr = getRField(elem)("024")
    //((getRField(elem)("024").map(getRSubfieldContent(_)("a")))).flatten.
    //  map(_.text)

  }

  def doiFeature(elem: Elem): immutable.Seq[String] = {

    getRField(elem)("024").filter(data => (data \@ "ind1" == "7" )).
      filter( data =>
      {
        (getNRSubfieldContent(data)("2"))
        match {
          case Some(v)  if v == "doi" => true
          case _ => false
        }
      }
      ).
      map(getRSubfieldContent(_)("a")).flatten.map(_.text)

  }

  def ismnFeature(elem: Elem): immutable.Seq[String] = {

    getRField(elem)("024").filter(data => (data \@ "ind1" == "2" )).
      map(getRSubfieldContent(_)("a")).flatten.map(_.text)
  }

  def musicIdFeature(elem: Elem): immutable.Seq[String] = {

    ((getRField(elem)("028").map(getRSubfieldContent(_)("a")))).flatten.
      map(_.text)

  }

  def formatFeature(elem: Elem): immutable.Seq[String] = {

    //((getRField(elem)("898").map(getRSubfieldContent(_)("a")))).flatten.
    //  map(_.text)
    //val t = getRField(elem)("898").map(getRSubfieldContent(_)("a")).flatten.map(_.text)
    ((getRField(elem)("898").map(getRSubfieldContent(_)("a")).flatten.map(_.text)))

  }


}

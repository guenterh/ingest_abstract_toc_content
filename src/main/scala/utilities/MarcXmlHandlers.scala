/*
 * Flink workflow for enriching volume records
 * Copyright (C) 2019  project swissbib <http://swissbib.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package utilities

import org.swissbib.slsp.NewFields
import utilities.UtilityTypes.{Field7xx, KeyedField, SeriesRecord, VolumeRecord}

import scala.xml.transform.{RewriteRule, RuleTransformer}
import scala.xml.{Elem, Node, NodeSeq, XML}


trait MarcXmlHandlers {

  /**
    * Parses line as MARC-XML record
    *
    * @param line line in file
    * @return MARC-XML record
    */
  protected def parseRecord(line: String): Elem = XML.loadString(line)

  /**
    * Serializes MARC-XML record as String
    *
    * @param record MARC-XML record
    * @return String
    */
  protected def serializeRecord(record: Elem): String = record.toString()

  /**
    * Gets record id as defined in Marc field 001
    *
    * @param xml MARC-XML record root element
    * @return Record id
    */
  protected def getField001Content(xml: Elem): String = {
    (xml \\ "controlfield")
      .find(cf => (cf \@ "tag") == "001")
      .get
      .text
  }



  /**
    * Gets Seq of control numbers as defined in Marc field 035$a
    *
    * @param xml Marc XML record root element
    * @return List of control numbers
    */
  protected def getField035Contents(xml: Elem): Seq[String] = {
    getRField(xml)("035").map(getNRSubfieldContent(_)("a")).map(t => t.get)
  }

  /**
    * Checks if field 839 exists in record
    *
    * @param xml Marc-XML record
    * @return true if field exists
    */
  protected def hasFields839(xml: Elem): Boolean = getRField(xml)("839").nonEmpty

  /**
    * Gets content of subfields 839v and 839w
    *
    * @param xml Marc XML record root element
    * @return List of control numbers
    */
  protected def getSubfields839vwContents(xml: Elem): Seq[(String, String)] = {
    getRField(xml)("839")
      .map(f => (getNRSubfieldContent(f)("v"), getNRSubfieldContent(f)("w")))
      .map {
        case (Some(v), Some(w)) => (v, w)
        case (None, Some(w)) => ("", w)
        case (Some(v), None) => (v, "")
        case _ => ("", "")
      }
  }

  /**
    * Gets content of repeatable datafield as `NodeSeq`
    *
    * @param rootElem  entire record as `Elem`
    * @param fieldName field name (tag) to be extracted
    * @return `NodeSeq` of datafields
    */
  protected def getRField(rootElem: Elem)(fieldName: String): NodeSeq =
    (rootElem \\ "datafield").filter(df => df \@ "tag" == fieldName)

  protected def getNRField(rootElem: Elem)(fieldName: String): Node =
    (rootElem \\ "datafield").filter(df => df \@ "tag" == fieldName).head


  protected def getNRControlfieldField(rootElem: Elem)(fieldName: String): Node =
    (rootElem \\ "controlfield").filter(df => df \@ "tag" == fieldName).head


  /**
    * Gets content of a non-repeatable subfield
    *
    * @param field        field as XML `Node`
    * @param subfieldName subfield code
    * @return optional content
    */
  protected def getNRSubfieldContent(field: Node) (subfieldName: String): Option[String] =
    (field \\ "subfield").find(s => s \@ "code" == subfieldName) match {
      case Some(v) => Some(v.text)
      case _ => None
    }

  protected def f1(field: Node, subfieldName: String): Option[String] =
    (field \\ "subfield").find(s => s \@ "code" == subfieldName) match {
      case Some(v) => Some(v.text)
      case _ => None
    }



  protected def getRSubfieldContent(field: Node)(subfieldName: String): NodeSeq =
    (field \\ "subfield").filter(s => s \@ "code" == subfieldName)

  protected def getAllSubfieldContent(field: Node): NodeSeq =
    (field \\ "subfield")



  protected def getRSubfieldContent1(field: Node)(subfieldName: String): NodeSeq =
    (field \\ "subfield").filter(s => s \@ "code" == subfieldName)


  /**
    * Adds a sequence of fields to record (used as a transformation rule)
    */
  protected val addFieldToRecord: Seq[Elem] => RewriteRule = e => new RewriteRule {
    override def transform(n: Node): Seq[Node] = n match {
      case elem: Elem if elem.label == "record" =>
        elem.copy(child = elem.child ++ e)
      case x => x
    }
  }

  /**
    * Filters out fields based on tag name of a `Seq[Elem]`
    *
    * @param filters filters to apply (fields with same tag and indicators will be filtered out)
    * @param field   record on which the filter should be applied
    * @return cleaned record
    */
  protected def filterDatafields(filters: Seq[Elem])(field: Node): Boolean =
    filters.exists(f => (field \ "@tag").text == (f \ "@tag").text)

  /**
    * Merges 8xx fields with record
    *
    * @param rec      entire record
    * @param field8xx 8xx fields to be merged
    * @return enriched record
    */
  protected def mergeField8xx(rec: Elem, field8xx: Seq[Elem]): Elem = {
    val transform = new RuleTransformer(addFieldToRecord(field8xx))
    transform(rec) match {
      case e: Elem => e
      case r => rec
    }
  }

  /**
    * Merges 7xx fields with record
    *
    * @param rec      entire record
    * @param field7xx 7xx fields to be merged
    * @return enriched record
    */
  protected def mergeField7xx(rec: Elem, field7xx: Seq[Elem]): Elem = {
    val reducedRec = <record>{rec.flatMap(_.child).filterNot(filterDatafields(field7xx)(_))}</record>
    val add = new RuleTransformer(addFieldToRecord(field7xx))
    add(reducedRec) match {
      case e: Elem => e
      case r => rec
    }
  }

  protected def remove839Fields(rec: Elem): Elem = {
    val field839: Elem = {
      <datafield tag="839" ind1=" " ind2="0"></datafield>
    }
    <record>{rec.flatMap(_.child).filterNot(filterDatafields(Seq(field839))(_))}</record>
  }

  /**
    * Extracts fields with tag 760, 765, 770, 772, 773, 775, 776, 777, 780, 785, and 787 from record
    *
    * @param rec record
    * @return List of fields
    */
  protected def extract7xxFields(rec: Elem): Seq[Field7xx] =
    Seq("760", "765", "770", "772", "773", "775", "776", "777", "780", "785", "787")
      .foldLeft[Seq[Field7xx]](Seq())((acc, field) => acc ++ getLocalRecId(rec, field))

  /**
    * Get id of belonging series from field 7xx##$w in volume record
    *
    * @param volume    volume record
    * @param fieldName field name (tag)
    * @return value found in subfield $w
    */
  private def getLocalRecId(volume: Elem, fieldName: String): Seq[Field7xx] = {
    val recId = getField001Content(volume)
    getRField(volume)(fieldName)
      .map {
        case field@(_: Elem) => Some((getNRSubfieldContent(field)("w"), field))
        case _ => None
      }
      .filter(idSubfieldTuple => idSubfieldTuple.isDefined && idSubfieldTuple.get._1.isDefined)
      .map(idSubfieldTuple => Field7xx(fieldName, recId, idSubfieldTuple.get._1.get, idSubfieldTuple.get._2))
  }


  def getKeyedField(id: String, records: Seq[(VolumeRecord, SeriesRecord)]): KeyedField = {
    val elems: Seq[Elem] = records
      .flatMap(e => {
        val concatenatedFields = NewFields.field800(e._1.sf839v, e._1.seriesId).buildFrom(e._2.record).getOrElse(Seq()) ++
          NewFields.field810(e._1.sf839v, e._1.seriesId).buildFrom(e._2.record).getOrElse(Seq()) ++
          NewFields.field811(e._1.sf839v, e._1.seriesId).buildFrom(e._2.record).getOrElse(Seq())
        if (concatenatedFields.isEmpty) {
          concatenatedFields ++ NewFields.field830(e._1.sf839v, e._1.seriesId).buildFrom(e._2.record).getOrElse(Seq())
        }
        else {
          concatenatedFields
        }
      })
      .map(e => e.asInstanceOf[Elem])
    KeyedField(id, elems)
  }
}

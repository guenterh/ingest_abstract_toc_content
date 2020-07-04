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

import utilities.UtilityTypes.{KeyedField, SeriesRecord, VolumeRecord}

import scala.xml.Elem

trait Transformators extends MarcXmlHandlers {
  /**
    * Checks if line is XML record
    *
    * @param record Potential record
    * @return true if XML record
    */
  def isRecord(record: String): Boolean =
    record.startsWith("<record>")

  /**
    * Checks for existence of subfield 839##$w, which indicates a volume record
    *
    * @param record MARC-XML record
    * @return true if subfield exists
    */
  def isVolumeRecord(record: Elem): Boolean = hasFields839(record)

  /**
    * Creates a `SeriesRecord` out of a MARC-XML record
    *
    * @param record MARC-XML record
    * @return a new `SeriesRecord`
    */
  def createSeriesRecords(record: Elem): Seq[SeriesRecord] = {
    getField035Contents(record)
      .map(field035 => SeriesRecord(getField001Content(record), field035, record))
  }

  /**
    * Creates a sequence of `StrippedVolumeRecord`s out of a MARC-XML record
    *
    * @param record MARC-XML record
    * @return sequence of `StrippedVolumeRecord`s
    */
  def createStrippedVolumeRecords(record: Elem): Seq[VolumeRecord] =
    getSubfields839vwContents(record)
      .map(sf839vw => VolumeRecord(getField001Content(record), sf839vw._2, sf839vw._1))

  /**
    * Applies a merge function on a MARC-XML record and a sequence of fields with the same tag (`KeyedField`)
    *
    * @param rec      MARC-XML record
    * @param field    sequence of fields with the same tag
    * @param mergeFun merge function
    * @return enriched MARC-XML record
    */
  def applyMerge(rec: Elem, field: KeyedField)(mergeFun: (Elem, Seq[Elem]) => Elem): Elem =
    if (field == null) {
      rec
    } else {
      mergeFun(rec, field.fields)
    }

}

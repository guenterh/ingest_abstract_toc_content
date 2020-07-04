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

import scala.xml.Elem

object UtilityTypes {

  /**
    * Contains list of all fields in a record with the same tag
    *
    * @param recId  record id
    * @param fields list of fields
    */
  case class KeyedField(recId: String,
                        fields: Seq[Elem])

  /**
    * A "bare" volume record only containing relevant information for finding the series the volume belongs to
    *
    * @param recId    record id of volume record
    * @param seriesId local record id of series record
    * @param sf839v   value of subfield 839##$v
    */
  case class VolumeRecord(recId: String,
                          seriesId: String,
                          sf839v: String)

  /**
    * A series record with record id and one local record id in separate fields (used for matching volume records)
    *
    * @param recId      record id of series record
    * @param localRecId one local record id (ie. one value of potentially several subfields 035##$a)
    * @param record     whole series record
    */
  case class SeriesRecord(recId: String,
                          localRecId: String,
                          record: Elem)

  /**
    * A single line of the lookup table for local records id -> record id in series records
    *
    * @param localRecId local record id
    * @param recId      record id
    */
  case class LocalKeyRecKeyEntry(localRecId: String,
                                 recId: String)

  /**
    * Intermediary type used for creating and filtering 7xx fields
    *
    * @param fieldTag     Tag value of field
    * @param recId        record id the field belongs to
    * @param seriesId     series id (either local record id or record id)
    * @param fieldContent Content of field
    */
  case class Field7xx(fieldTag: String,
                      recId: String,
                      seriesId: String,
                      fieldContent: Elem)


}

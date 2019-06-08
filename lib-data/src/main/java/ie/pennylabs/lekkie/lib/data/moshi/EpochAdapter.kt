/*
 * Copyright (C) 2018 Elliot Tormey
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ie.pennylabs.lekkie.lib.data.moshi

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.ToJson
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class EpochTime

private val dateFormatter =
  DateTimeFormatter
    .ofPattern("dd/MM/yyyy HH:mm")
    .withZone(ZoneId.of("Europe/Dublin"))

class EpochAdapter {
  @FromJson
  @EpochTime
  fun fromJson(value: String): Long = ZonedDateTime.parse(value, dateFormatter).toEpochSecond()

  @ToJson
  fun toJson(@EpochTime value: Long): String? = dateFormatter.format(Instant.ofEpochSecond(value))
}
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

package ie.pennylabs.lekkie.data.model

import androidx.lifecycle.LiveData
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.squareup.moshi.Json
import ie.pennylabs.lekkie.data.model.Outage.Key.COUNTY
import ie.pennylabs.lekkie.data.model.Outage.Key.ID
import ie.pennylabs.lekkie.data.model.Outage.Key.START_TIME
import ie.pennylabs.lekkie.data.model.Outage.Key.TABLE_NAME
import ie.pennylabs.lekkie.data.moshi.EpochTime

@Entity(tableName = Outage.Key.TABLE_NAME)
data class Outage(
  @PrimaryKey
  @field:Json(name = "outageId")
  @ColumnInfo(name = ID)
  val id: String,
  @field:Json(name = "outageType")
  val type: String,
  @field:EpochTime
  val estRestoreTime: Long,
  val location: String,
  @ColumnInfo(name = COUNTY)
  val county: String?,
  val numCustAffected: String,
  @field:EpochTime
  @ColumnInfo(name = "start_time")
  val startTime: Long,
  val statusMessage: String,
  val point: Point
) {
  object Key {
    const val TABLE_NAME = "outage"
    const val ID = "id"
    const val START_TIME = "start_time"
    const val COUNTY = "county"
  }
}

@Dao
interface OutageDao {
  @Query("SELECT * FROM $TABLE_NAME ORDER BY $START_TIME")
  fun fetchAll(): LiveData<List<Outage>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(outage: Outage)

  @Query("UPDATE $TABLE_NAME SET $COUNTY=:county WHERE $ID = :id")
  fun updateCounty(county: String, id: String)
}

data class OutageConcise(
  @field:Json(name = "outageId")
  val id: String
)
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
import ie.pennylabs.lekkie.data.model.Outage.Key.START_TIME
import ie.pennylabs.lekkie.data.model.Outage.Key.TABLE_NAME
import ie.pennylabs.lekkie.data.moshi.EpochTime


@Entity(tableName = Outage.Key.TABLE_NAME)
data class Outage(
  @PrimaryKey
  @field:Json(name = "outageId")
  val id: String,
  @field:Json(name = "outageType")
  val type: String,
  @field:EpochTime
  val estRestoreTime: Long,
  val location: String,
  val numCustAffected: String,
  @field:EpochTime
  @ColumnInfo(name = "start_time")
  val startTime: Long,
  val statusMessage: String,
  val point: Point
) {
  object Key {
    const val TABLE_NAME = "outage"
    const val START_TIME = "start_time"
  }
}

/*
* {"outageId":"1652560","outageType":"Fault","point":{"coordinates":"53.450282774946,-7.191009303344"},"estRestoreTime":"15/06/2018 22:00","location":"Ballinderry","numCustAffected":"33","startTime":"15/06/2018 18:09","statusMessage":"We apologise for the loss of supply. We are currently working to repair a fault affecting your premises and will restore power as quickly as possible."}*/

@Dao
interface OutageDao {
  @Query("SELECT * FROM $TABLE_NAME ORDER BY $START_TIME")
  fun fetchAll(): LiveData<List<Outage>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(outage: Outage)
}

data class OutageConcise(
  @field:Json(name = "outageId")
  val id: String
)

/*{
    "outageId": "1650887",
    "outageType": "Fault",
    "point": {
        "coordinates": "54.108286954538,-7.758841138269"
    },
    "estRestoreTime": "14/06/2018 23:30",
    "location": "Carrigallen",
    "numCustAffected": "24",
    "startTime": "14/06/2018 08:31",
    "statusMessage": "We apologise for the loss of supply. We are currently working to repair a fault affecting your premises and will restore power as quickly as possible."
}*/

/*{
  "outageId": "1650887",
  "outageType": "Fault",
  "point": {
      "coordinates": "54.108286954538,-7.758841138269"
  }
}*/
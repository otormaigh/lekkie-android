package ie.pennylabs.lekkie.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ie.pennylabs.lekkie.data.model.Outage
import ie.pennylabs.lekkie.data.model.OutageDao

@Database(entities = [Outage::class], version = 1)
@TypeConverters(PointTypeConverter::class)
abstract class LekkieDatabase : RoomDatabase() {
  abstract fun outageDao(): OutageDao
}
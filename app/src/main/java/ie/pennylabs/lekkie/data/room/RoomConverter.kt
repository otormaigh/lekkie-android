package ie.pennylabs.lekkie.data.room

import androidx.room.TypeConverter
import ie.pennylabs.lekkie.data.model.Point
import ie.pennylabs.lekkie.di.DataModule

object PointTypeConverter {
  private val adapter = DataModule.provideMoshi().adapter<Point>(Point::class.java)

  @TypeConverter
  @JvmStatic
  fun fromJson(json: String): Point = adapter.fromJson(json)!!

  @TypeConverter
  @JvmStatic
  fun toJson(data: Point): String = adapter.toJson(data)
}
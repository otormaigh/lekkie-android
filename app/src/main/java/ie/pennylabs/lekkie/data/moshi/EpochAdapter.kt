package ie.pennylabs.lekkie.data.moshi

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.util.*

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class EpochTime

private const val DATE_FORMAT = "dd/MM/yyyy HH:mm"

class EpochAdapter {
  @FromJson
  @EpochTime
  fun fromJson(value: String): Long = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).parse(value).time

  @ToJson
  fun toJson(@EpochTime value: Long) = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(value)
}
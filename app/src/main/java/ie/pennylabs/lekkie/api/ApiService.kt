package ie.pennylabs.lekkie.api

import ie.pennylabs.lekkie.data.model.Outage
import ie.pennylabs.lekkie.data.model.OutageMessage
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
  @GET("outages/{id}/")
  fun getOutage(
    @Path("id") id: String,
    @Query("_") delta: Long = System.currentTimeMillis())
    : Call<Outage>

  @GET("outages/")
  fun getOutages(
    @Query("_") delta: Long = System.currentTimeMillis())
    : Call<OutageMessage>
}
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

package ie.pennylabs.lekkie.lib.data.worker

import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.work.*
import ie.pennylabs.lekkie.lib.data.api.ApiService
import ie.pennylabs.lekkie.lib.data.model.Outage
import ie.pennylabs.lekkie.lib.data.model.OutageDao
import ie.pennylabs.lekkie.lib.toolbox.extension.shouldRefresh
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.TimeUnit

class ApiWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {
  lateinit var api: ApiService
  lateinit var outageDao: OutageDao
  lateinit var geocoder: Geocoder

  override suspend fun doWork(): Result = try {
    val response = api.getOutages()
    outageDao.insert(response.outageMessage.mapNotNull { fetchDetail(it.id) })

    Result.success()
  } catch (e: Exception) {
    Timber.e(e)
    Result.failure()
  }

  private suspend fun fetchDetail(id: String): Outage? {
    val savedOutage = outageDao.fetch(id)
    if (savedOutage == null || savedOutage.shouldRefresh) {
      val response = api.getOutage(id)
      return updateCounty(response.apply { delta = System.currentTimeMillis() })
    }
    return savedOutage
  }

  private fun updateCounty(outage: Outage): Outage {
    if (outage.county?.isNotEmpty() == true) return outage
    else try {
      val address = geocoder.getFromLocation(outage.point.latitude, outage.point.longitude, 1).firstOrNull()
      outage.apply {
        county = address?.adminArea ?: county ?: ""
        location = address?.locality ?: location ?: ""
      }
    } catch (e: IOException) {
      Timber.e("fetchCounty -> ${Log.getStackTraceString(e)}")
    }

    return outage
  }

  companion object {
    const val RECURRING_TAG = "recurring_request"

    fun oneTimeRequest(context: Context): LiveData<WorkInfo> {
      val workRequest = OneTimeWorkRequestBuilder<ApiWorker>().build()
      WorkManager.getInstance(context).enqueue(workRequest)
      return WorkManager.getInstance(context).getWorkInfoByIdLiveData(workRequest.id)
    }

    fun recurringRequest(context: Context) {
      _recurringRequest(context, ExistingPeriodicWorkPolicy.KEEP, 1, TimeUnit.HOURS)
    }

    fun updateRepeatInterval(context: Context, repeatInterval: Long, repeatIntervalTimeUnit: TimeUnit) {
      _recurringRequest(context, ExistingPeriodicWorkPolicy.REPLACE, repeatInterval, repeatIntervalTimeUnit)
    }

    @Suppress("detekt.FunctionNaming")
    private fun _recurringRequest(
      context: Context,
      workPolicy: ExistingPeriodicWorkPolicy,
      repeatInterval: Long,
      repeatIntervalTimeUnit: TimeUnit
    ) {
      WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "unique_recurring_worker",
        workPolicy,
        PeriodicWorkRequestBuilder<ApiWorker>(repeatInterval, repeatIntervalTimeUnit)
          .addTag(RECURRING_TAG)
          .addTag("interval:${TimeUnit.MILLISECONDS.convert(repeatInterval, repeatIntervalTimeUnit)}")
          .build()
      )
    }
  }
}
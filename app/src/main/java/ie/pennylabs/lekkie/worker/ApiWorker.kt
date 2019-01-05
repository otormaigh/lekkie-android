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

package ie.pennylabs.lekkie.worker

import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import ie.pennylabs.lekkie.api.ApiService
import ie.pennylabs.lekkie.data.model.Outage
import ie.pennylabs.lekkie.data.model.OutageDao
import ie.pennylabs.lekkie.toolbox.extension.shouldRefresh
import ru.gildor.coroutines.retrofit.awaitResult
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.TimeUnit

class ApiWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {
  lateinit var api: ApiService
  lateinit var outageDao: OutageDao
  lateinit var geocoder: Geocoder

  override suspend fun doWork(): Result {
    when (val result = api.getOutages().awaitResult()) {
      is ru.gildor.coroutines.retrofit.Result.Ok ->
        outageDao.insert(result.value.outageMessage.mapNotNull { fetchDetail(it.id) })
      else -> return Result.failure()
    }

    return Result.success()
  }

  private suspend fun fetchDetail(id: String): Outage? {
    val savedOutage = outageDao.fetch(id)
    if (savedOutage == null || savedOutage.shouldRefresh) {
      val result = api.getOutage(id).awaitResult()
      when (result) {
        is ru.gildor.coroutines.retrofit.Result.Ok ->
          return updateCounty(result.value.apply { delta = System.currentTimeMillis() })
        else -> Timber.e("result -> $result")
      }
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
    fun oneTimeRequest(): LiveData<WorkInfo> {
      val workRequest = OneTimeWorkRequestBuilder<ApiWorker>().build()
      WorkManager.getInstance().enqueue(workRequest)
      return WorkManager.getInstance().getWorkInfoByIdLiveData(workRequest.id)
    }

    fun recurringRequest() {
      WorkManager.getInstance().enqueueUniquePeriodicWork(
        "unique_recurring_worker",
        ExistingPeriodicWorkPolicy.KEEP,
        PeriodicWorkRequestBuilder<ApiWorker>(3, TimeUnit.HOURS)
          .addTag("recurring_request")
          .build())
    }
  }
}
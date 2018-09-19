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

package ie.pennylabs.lekkie.feature.list

import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ie.pennylabs.lekkie.api.ApiService
import ie.pennylabs.lekkie.data.model.Outage
import ie.pennylabs.lekkie.data.model.OutageDao
import ie.pennylabs.lekkie.toolbox.extension.isStale
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import ru.gildor.coroutines.retrofit.Result
import ru.gildor.coroutines.retrofit.awaitResult
import timber.log.Timber
import java.io.IOException

class OutageListViewModel(
  private val fetcher: ApiService,
  private val persister: OutageDao,
  private val geocoder: Geocoder
) : ViewModel() {
  private val job = Job()
  private val _outages: MutableLiveData<LiveData<List<Outage>>> = MutableLiveData()
  val outages: LiveData<LiveData<List<Outage>>> = _outages

  init {
    queryQao(null)
    fetchOutages()
  }

  override fun onCleared() {
    super.onCleared()
    job.cancel()
  }

  fun fetchOutages() {
    launch(job) {
      val result = fetcher.getOutages().awaitResult()
      when (result) {
        is Result.Ok -> result.value.outageMessage
          .sortedByDescending { it.id }
          .forEach { fetchOutageDetail(it.id.toString()) }
        else -> Timber.e("result -> $result")
      }
    }
  }

  fun queryQao(query: String?) {
    launch(job) {
      _outages.postValue(if (query == null) {
        persister.fetchAll()
      } else {
        persister.searchForCountyAndLocation("%$query%")
      })
    }
  }

  fun updateOutageCounty(outage: Outage) {
    if (outage.county?.isNotEmpty() == true) return
    launch(job) {
      try {
        geocoder.getFromLocation(outage.point.latitude, outage.point.longitude, 1)
          .firstOrNull()
          ?.let { address ->
            persister.update(address.adminArea, address.locality, outage.id)
          }
      } catch (e: IOException) {
        Timber.e("fetchCounty -> ${Log.getStackTraceString(e)}")
      }
    }
  }

  private suspend fun fetchOutageDetail(id: String) = launch(job) {
    val savedOutage = persister.fetch(id)
    if (savedOutage == null || savedOutage.isStale) {
      val result = fetcher.getOutage(id).awaitResult()
      when (result) {
        is Result.Ok -> {
          persister.insert(result.value.apply { delta = System.currentTimeMillis() })
          updateOutageCounty(result.value)
        }
        else -> Timber.e("result -> $result")
      }
    }
  }
}
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

import androidx.lifecycle.ViewModel
import ie.pennylabs.lekkie.api.ApiService
import ie.pennylabs.lekkie.data.model.OutageDao
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import ru.gildor.coroutines.retrofit.Result
import ru.gildor.coroutines.retrofit.awaitResult
import timber.log.Timber

class OutageListViewModel(
  private val fetcher: ApiService,
  private val persister: OutageDao
) : ViewModel() {
  private val job = Job()
  val liveData = persister.fetchAll()

  init {
    fetchOutages()
  }

  override fun onCleared() {
    super.onCleared()
    job.cancel()
  }

  private fun fetchOutages() {
    launch(job) {
      val result = fetcher.getOutages().awaitResult()
      when (result) {
        is Result.Ok -> result.value.outageMessage.forEach { fetchOutageDetail(it.id) }
        else -> Timber.e("result -> $result")
      }
    }
  }

  private suspend fun fetchOutageDetail(id: String) = launch(job) {
    val result = fetcher.getOutage(id).awaitResult()
    when (result) {
      is Result.Ok -> persister.insert(result.value)
      else -> Timber.e("result -> $result")
    }
  }
}
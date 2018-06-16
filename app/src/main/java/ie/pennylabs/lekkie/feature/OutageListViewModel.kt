package ie.pennylabs.lekkie.feature

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
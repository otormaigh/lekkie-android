package ie.pennylabs.lekkie.api

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.QueueDispatcher
import okhttp3.mockwebserver.RecordedRequest
import timber.log.Timber
import java.nio.charset.StandardCharsets
import java.util.*

class ApiResponseDispatcher : QueueDispatcher() {
  override fun dispatch(request: RecordedRequest): MockResponse {
    return MockResponse().apply {
      val requestedMethod = request.method.toLowerCase(Locale.getDefault())
      val fileName = requestedMethod +
        request.path
          .replace("/", "_")
          .replaceAfter("?", "")
          .dropLast(2) + ".json"
      Timber.e("fileName = $fileName")

      setBody(readFile(fileName))
      setResponseCode(200)
    }
  }

  private fun readFile(fileName: String): String {
    ApiResponseDispatcher::class.java.getResourceAsStream("/$fileName").use {
      val size = it.available()
      val buffer = ByteArray(size)
      it.read(buffer)
      it.close()

      return String(buffer, StandardCharsets.UTF_8)
    }
  }
}
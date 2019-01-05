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

package ie.pennylabs.lekkie.api

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.QueueDispatcher
import okhttp3.mockwebserver.RecordedRequest
import timber.log.Timber
import java.nio.charset.StandardCharsets
import java.util.*

class ApiResponseDispatcher : QueueDispatcher() {
  override fun dispatch(request: RecordedRequest): MockResponse =
    MockResponse().apply {
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

  private fun readFile(fileName: String): String =
    ApiResponseDispatcher::class.java.getResourceAsStream("/$fileName")?.use {
      val size = it.available()
      val buffer = ByteArray(size)
      it.read(buffer)
      it.close()

      return String(buffer, StandardCharsets.UTF_8)
    } ?: ""
}
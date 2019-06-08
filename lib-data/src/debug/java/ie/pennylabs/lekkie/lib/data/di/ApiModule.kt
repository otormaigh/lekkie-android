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

package ie.pennylabs.lekkie.lib.data.di

import android.os.StrictMode
import dagger.Module
import dagger.Provides
import ie.pennylabs.lekkie.lib.data.api.ApiResponseDispatcher
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockWebServer

@Module(includes = [BaseApiModule::class])
object ApiModule {
  @Provides
  @JvmStatic
  fun provideMockWebServer(): MockWebServer {
    StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())
    return MockWebServer().apply {
      dispatcher = ApiResponseDispatcher()
    }
  }

  @Provides
  @JvmStatic
  fun provideBaseUrl(mockWebServer: MockWebServer): HttpUrl = mockWebServer.url("/")
}
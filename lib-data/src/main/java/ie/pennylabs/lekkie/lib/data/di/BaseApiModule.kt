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

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import ie.pennylabs.lekkie.lib.data.BuildConfig
import ie.pennylabs.lekkie.lib.data.api.ApiService
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

private const val TIMEOUT = 30_000L

@Module
object BaseApiModule {
  @Provides
  @JvmStatic
  fun provideRetrofit(httpUrl: HttpUrl, moshi: Moshi, okHttpClient: OkHttpClient): Retrofit =
    Retrofit.Builder()
      .baseUrl(httpUrl)
      .client(okHttpClient)
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()


  @Provides
  @JvmStatic
  fun provideOkHttp(): OkHttpClient =
    OkHttpClient.Builder()
      .protocols(listOf(Protocol.HTTP_1_1))
      .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
      .readTimeout(TIMEOUT, TimeUnit.SECONDS)
      .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
      .addNetworkInterceptor(HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.NONE
        if (BuildConfig.DEBUG) level = HttpLoggingInterceptor.Level.BODY
      }).build()

  @Provides
  @JvmStatic
  fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)
}

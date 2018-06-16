package ie.pennylabs.lekkie.di

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import ie.pennylabs.lekkie.BuildConfig
import ie.pennylabs.lekkie.api.ApiService
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private const val TIMEOUT = 30_000L

@Module
object ApiModule {

  @Provides
  @JvmStatic
  fun provideBaseUrl(): HttpUrl = HttpUrl.parse(BuildConfig.BASE_URL)!!

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
      .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
      .readTimeout(TIMEOUT, TimeUnit.SECONDS)
      .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
      .addNetworkInterceptor(HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.NONE
        if (BuildConfig.DEBUG) level = HttpLoggingInterceptor.Level.BODY
      }).build()

  @Provides
  @Singleton
  @JvmStatic
  fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)
}
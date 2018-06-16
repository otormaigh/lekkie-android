/*
package ie.pennylabs.lekkie.di

import dagger.Module
import dagger.Provides
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockWebServer

@Module(includes = [ApiModule::class])
object DebugApiModule {
  @Provides
  @JvmStatic
  fun provideMockWebServer(): MockWebServer = MockWebServer()

  @Provides
  @JvmStatic
  fun provideBaseUrl(mockWebServer: MockWebServer): HttpUrl = mockWebServer.url("/")
}*/

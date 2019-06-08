package ie.pennylabs.lekkie.lib.data.di

import dagger.Module
import dagger.Provides
import ie.pennylabs.lekkie.lib.data.BuildConfig
import okhttp3.HttpUrl

@Module(includes = [BaseApiModule::class])
object ApiModule {
  @Provides
  @JvmStatic
  fun provideBaseUrl(): HttpUrl = HttpUrl.get(BuildConfig.BASE_URL)
}
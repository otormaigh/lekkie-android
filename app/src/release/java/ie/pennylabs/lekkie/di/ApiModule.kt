package ie.pennylabs.lekkie.di

import dagger.Module
import dagger.Provides
import ie.pennylabs.lekkie.BuildConfig
import okhttp3.HttpUrl

@Module(includes = [BaseApiModule::class])
object ApiModule {
  @Provides
  @JvmStatic
  fun provideBaseUrl(): HttpUrl = HttpUrl.parse(BuildConfig.BASE_URL)!!
}
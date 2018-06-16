package ie.pennylabs.lekkie.di

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import ie.pennylabs.lekkie.LekkieApplication
import javax.inject.Singleton

@Singleton
@Component(modules = [
  AndroidInjectionModule::class,
  ApiModule::class,
  DataModule::class,
  ActivityBuilder::class])
interface AppComponent {
  @Component.Builder
  interface Builder {
    @BindsInstance
    fun application(application: LekkieApplication): Builder

    fun build(): AppComponent
  }

  fun inject(application: LekkieApplication)
}
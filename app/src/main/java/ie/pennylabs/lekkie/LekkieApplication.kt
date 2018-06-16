package ie.pennylabs.lekkie

import android.app.Activity
import android.app.Application
import android.os.StrictMode
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import ie.pennylabs.lekkie.di.DaggerAppComponent
import timber.log.Timber
import javax.inject.Inject

class LekkieApplication : Application(), HasActivityInjector {
  @Inject
  lateinit var activityInjector: DispatchingAndroidInjector<Activity>

  override fun onCreate() {
    super.onCreate()

    @Suppress("ConstantConditionIf")
    if (BuildConfig.DEBUG) {
      Timber.plant(object : Timber.DebugTree() {
        override fun createStackElementTag(element: StackTraceElement): String {
          return "(${element.fileName}:${element.lineNumber})"
        }
      })

      // MockWebServer
      StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())
    }

    DaggerAppComponent.builder()
      .application(this)
      .build()
      .inject(this)
  }

  override fun activityInjector(): AndroidInjector<Activity> = activityInjector
}
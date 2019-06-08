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

package ie.pennylabs.lekkie

import android.app.Activity
import android.app.Application
import android.os.StrictMode
import androidx.fragment.app.Fragment
import androidx.work.Configuration
import androidx.work.WorkManager
import com.crashlytics.android.core.CrashlyticsCore
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.support.HasSupportFragmentInjector
import ie.pennylabs.lekkie.data.di.DaggerDataComponent
import ie.pennylabs.lekkie.di.DaggerAppComponent
import ie.pennylabs.lekkie.worker.ApiWorker
import ie.pennylabs.lekkie.worker.LekkieWorkerFactory
import io.fabric.sdk.android.Fabric
import timber.log.Timber
import javax.inject.Inject

class LekkieApplication : Application(), HasActivityInjector, HasSupportFragmentInjector {
  @Inject
  lateinit var activityInjector: DispatchingAndroidInjector<Activity>
  @Inject
  lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>
  @Inject
  lateinit var lekkieWorkerFactory: LekkieWorkerFactory

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

    Fabric.with(this, CrashlyticsCore.Builder()
      .disabled(BuildConfig.DEBUG)
      .build())

    val dataComponent = DaggerDataComponent.builder()
      .application(this)
      .build()

    DaggerAppComponent.builder()
      .application(this)
      .dataComponent(dataComponent)
      .build()
      .inject(this)

    AndroidThreeTen.init(this)

    initWorkManager()
  }

  override fun activityInjector(): AndroidInjector<Activity> = activityInjector
  override fun supportFragmentInjector(): AndroidInjector<Fragment> = supportFragmentInjector

  private fun initWorkManager() {
    WorkManager.initialize(this, Configuration.Builder()
      .setWorkerFactory(lekkieWorkerFactory)
      .build())

    if (BuildConfig.DEBUG) ApiWorker.oneTimeRequest(this)
    else ApiWorker.recurringRequest(this)
  }
}
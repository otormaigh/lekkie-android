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
import com.bluelinelabs.conductor.Controller
import com.christianbahl.conductor.HasControllerInjector
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import ie.pennylabs.lekkie.di.DaggerAppComponent
import timber.log.Timber
import javax.inject.Inject

class LekkieApplication : Application(), HasActivityInjector, HasControllerInjector {
  @Inject
  lateinit var activityInjector: DispatchingAndroidInjector<Activity>

  @Inject
  lateinit var controllerInjector: DispatchingAndroidInjector<Controller>

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

    AndroidThreeTen.init(this)
  }

  override fun activityInjector(): AndroidInjector<Activity> = activityInjector
  override fun controllerInjector(): DispatchingAndroidInjector<Controller> = controllerInjector
}
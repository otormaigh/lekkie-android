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

package ie.pennylabs.lekkie.lib.data.worker

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import ie.pennylabs.lekkie.lib.data.BuildConfig

object LekkieWorkManager {
  fun init(application: Application, lekkieWorkerFactory: LekkieWorkerFactory) {
    WorkManager.initialize(application, Configuration.Builder()
      .setWorkerFactory(lekkieWorkerFactory)
      .build())

    if (BuildConfig.DEBUG) ApiWorker.oneTimeRequest(application)
    else ApiWorker.recurringRequest(application)
  }
}
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

import android.content.Context
import android.location.Geocoder
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import ie.pennylabs.lekkie.lib.data.api.ApiService
import ie.pennylabs.lekkie.lib.data.room.LekkieDatabase
import javax.inject.Inject

class LekkieWorkerFactory
@Inject
constructor(private val api: ApiService,
            private val database: LekkieDatabase,
            private val geocoder: Geocoder) : WorkerFactory() {

  override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): ListenableWorker? {
    val instance = Class.forName(workerClassName)
      .asSubclass(ListenableWorker::class.java)
      .getDeclaredConstructor(Context::class.java, WorkerParameters::class.java)
      .newInstance(appContext, workerParameters)

    when (instance) {
      is ApiWorker -> {
        instance.api = api
        instance.outageDao = database.outageDao()
        instance.geocoder = geocoder
      }
    }
    return instance
  }
}
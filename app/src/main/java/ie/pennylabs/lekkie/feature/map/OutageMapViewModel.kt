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

package ie.pennylabs.lekkie.feature.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ie.pennylabs.lekkie.arch.BaseViewModel
import ie.pennylabs.lekkie.data.model.Outage
import ie.pennylabs.lekkie.data.model.OutageDao
import kotlinx.coroutines.launch
import javax.inject.Inject

class OutageMapViewModel
@Inject
constructor(private val outageDao: OutageDao) : BaseViewModel() {
  val outages: LiveData<List<Outage>>
    get() {
      val resultLiveData = MutableLiveData<List<Outage>>()
      launch {
        resultLiveData.postValue(outageDao.fetchAll())
      }
      return resultLiveData
    }
}
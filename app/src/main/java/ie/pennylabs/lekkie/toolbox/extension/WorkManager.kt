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

package ie.pennylabs.lekkie.toolbox.extension

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.work.WorkManager

fun WorkManager.intervalOfUniqueWork(tag: String): LiveData<Long> {
  val sourceLiveData = WorkManager.getInstance().getWorkInfosByTagLiveData(tag)
  return Transformations.map(sourceLiveData) {
    require(it.size <= 1) { "More than one unique work found with this tag '$tag'" }
    it.firstOrNull()
      ?.tags
      ?.firstOrNull { tag -> tag.contains("interval") }
      ?.replace("interval:", "")
      ?.toLong() ?: 0
  }
}
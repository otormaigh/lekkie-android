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

package ie.pennylabs.lekkie.feature.info

import android.os.Environment
import androidx.lifecycle.LiveData
import ie.pennylabs.lekkie.arch.BaseViewModel
import ie.pennylabs.lekkie.toolbox.SingleLiveEvent
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class InfoViewModel : BaseViewModel() {
  private val _showToast = SingleLiveEvent<String>()
  val showToast: LiveData<String> get() = _showToast

  fun exportDatabase(databaseFile: File?) {
    launch(coroutineContext + Dispatchers.Main) {
      val backupFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "lekkie_${System.currentTimeMillis()}.db")
      if (databaseFile?.exists() == true) {
        FileInputStream(databaseFile).channel.use { src ->
          FileOutputStream(backupFile).channel.use { dst ->
            dst.transferFrom(src, 0, src.size())
          }
          _showToast.postValue("Export success")
        }
      } else {
        _showToast.postValue("Database does not exist")
      }
    }
  }
}
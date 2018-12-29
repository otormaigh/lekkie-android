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

import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

fun Fragment.isPermissiontGranted(permission: String): Boolean =
  ActivityCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED

fun Fragment.requireApplicationContext(): Context = context?.applicationContext.takeIf { it != null }
  ?: throw NullPointerException("Fragment : $this not attached to an Activity")

fun Fragment.toast(message: String?, length: Int = Toast.LENGTH_LONG) {
  if (message?.isNotEmpty() == true) Toast.makeText(requireContext(), message, length).show()
}

val Fragment.applicationContext: Context?
  get() = context?.applicationContext
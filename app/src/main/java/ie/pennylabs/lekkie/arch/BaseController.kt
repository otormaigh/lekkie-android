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

package ie.pennylabs.lekkie.arch

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStore
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.ControllerLifecycleOwner

abstract class BaseController : Controller, LifecycleOwner {
  val viewModelStore = ViewModelStore()
  val lifecycleOwner = ControllerLifecycleOwner(this)

  constructor() : super()
  constructor(bundle: Bundle) : super(bundle)

  override fun onDestroy() {
    super.onDestroy()
    viewModelStore.clear()
  }

  override fun getLifecycle(): Lifecycle = lifecycleOwner.lifecycle
}

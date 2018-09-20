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

import android.view.View
import com.bluelinelabs.conductor.ViewModelController
import com.christianbahl.conductor.ConductorInjection
import kotlinx.coroutines.experimental.Job

abstract class BaseController : ViewModelController() {
  val onDetachJob = Job()

  override fun onAttach(view: View) {
    ConductorInjection.inject(this)
    super.onAttach(view)
  }

  override fun onDetach(view: View) {
    super.onDetach(view)
    onDetachJob.cancel()
  }
}
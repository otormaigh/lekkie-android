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

package ie.pennylabs.lekkie.toolbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import ie.pennylabs.lekkie.arch.BaseFragment
import kotlin.reflect.KProperty

class LazyViewModel<VM : ViewModel>(private val viewModel: Class<VM>) {
  operator fun getValue(fragment: BaseFragment, prop: KProperty<*>): VM =
    ViewModelProviders.of(fragment, fragment.viewModelFactory).get(viewModel)
}
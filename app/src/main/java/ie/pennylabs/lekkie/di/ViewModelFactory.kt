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

package ie.pennylabs.lekkie.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class ViewModelFactory
@Inject
constructor(
  private val providers: Map<Class<out ViewModel>,
    @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {

  override fun <T : ViewModel> create(modelClass: Class<T>): T =
    requireNotNull(getProvider(modelClass).get()) {
      "Provider for '$modelClass' returned null"
    }

  @Suppress("UNCHECKED_CAST")
  private fun <T : ViewModel> getProvider(modelClass: Class<T>): Provider<T> =
    try {
      requireNotNull(providers[modelClass] as Provider<T>) {
        "No ViewModel provider is bound for class '$modelClass'"
      }
    } catch (e: ClassCastException) {
      Timber.e(e)
      error("Wrong provider type registered for ViewModel type '$modelClass'")
    }
}
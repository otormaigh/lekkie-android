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
@file:Suppress("detekt.StringLiteralDuplication")
package ie.pennylabs.lekkie.toolbox.extension

import android.content.Context
import com.bluelinelabs.conductor.Controller

fun Controller.requireContext(): Context = view?.context.takeIf { it != null }
  ?: throw NullPointerException("Controller : $this not attached to an Activity")

fun Controller.requireActivity(): Context = activity.takeIf { it != null }
  ?: throw NullPointerException("Controller : $this not attached to an Activity")

fun Controller.requireApplicationContext(): Context = applicationContext.takeIf { it != null }
  ?: throw NullPointerException("Controller : $this not attached to an Activity")
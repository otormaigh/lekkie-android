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

package ie.pennylabs.lekkie.plugin.toolbox

import ie.pennylabs.lekkie.plugin.toolbox.extension.runCommand

object BuildConst {
  object Git {
    val shortHash = "git rev-parse --short HEAD".runCommand()?.trim() ?: ""
  }

  object Version {
    private const val major = 0
    private const val minor = 2
    private const val patch = 0
    private val build = System.getenv("CIRCLE_BUILD_NUM")?.toInt() ?: 1

    val name = "$major.$minor.$patch-${Git.shortHash}"
    val code = major * 10000000 + minor * 100000 + patch * 1000 + build
  }
}
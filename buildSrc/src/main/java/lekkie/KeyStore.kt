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

package lekkie

import org.gradle.api.Project

object KeyStore {
  const val RELEASE_STORE = "../signing/release.keystore"
  const val DEBUG_STORE = "../signing/debug.keystore"
  const val PLAY_JSON = "../signing/play.json"

  val Project.STORE_PASSWORD: String get() = properties["store_password"] as String
  val Project.KEY_ALIAS: String get() = properties["key_alias"] as String
  val Project.KEY_PASSWORD: String get() = properties["key_password"] as String
}
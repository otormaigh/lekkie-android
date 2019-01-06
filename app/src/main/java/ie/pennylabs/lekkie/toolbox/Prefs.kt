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

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import ie.pennylabs.lekkie.BuildConfig

val Context.prefs: SharedPreferences
  get() = getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)

private const val HAS_ACCEPTED_GDPR = "has_accepted_gdpr"
var SharedPreferences.hasAcceptedGdpr: Boolean
  set(value) {
    edit { putBoolean(HAS_ACCEPTED_GDPR, value) }
  }
  get() = getBoolean(HAS_ACCEPTED_GDPR, false)

private const val ENABLE_PERFORMANCE = "enable_performance"
var SharedPreferences.enablePerformance: Boolean
  set(value) {
    edit { putBoolean(ENABLE_PERFORMANCE, value) }
  }
  get() = getBoolean(ENABLE_PERFORMANCE, false)

private const val ENABLE_CRASH_REPORTING = "enable_crash_reporting"
var SharedPreferences.enableCrashReporting: Boolean
  set(value) {
    edit { putBoolean(ENABLE_CRASH_REPORTING, value) }
  }
  get() = getBoolean(ENABLE_CRASH_REPORTING, true)

private const val ENABLE_ANALYTICS = "enable_analytics"
var SharedPreferences.enableAnalytics: Boolean
  set(value) {
    edit { putBoolean(ENABLE_ANALYTICS, value) }
  }
  get() = getBoolean(ENABLE_ANALYTICS, false)

private const val SYNC_INTERVAL = "sync_interval"
var SharedPreferences.syncInterval: Long
  set(value) {
    edit { putLong(SYNC_INTERVAL, value) }
  }
  get() = getLong(SYNC_INTERVAL, 0L)
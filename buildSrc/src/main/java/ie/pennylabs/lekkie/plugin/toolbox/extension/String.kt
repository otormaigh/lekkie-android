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

package ie.pennylabs.lekkie.plugin.toolbox.extension

import java.io.IOException
import java.util.concurrent.TimeUnit


fun String.runCommand(): String? {
  return try {
    /*
    \\s         // Split on whitespace
    (?=         // Followed by
      (?:       // Start a non-capture group
        [^\']*  // 0 or more non-quote characters
        \"      // 1 quote
        [^\']*  // 0 or more non-quote characters
        \'      // 1 quote
      )*        // 0 or more repetition of non-capture group (multiple of 2 quotes will be even)
      [^\']*    // Finally 0 or more non-quotes
      $         // Till the end  (This is necessary, else every comma will satisfy the condition)
    )           // End look-ahead
    */
    val parts = split(
      """\s(?=(?:[^\']*\'[^\']*\')*[^\']*$)""".toRegex())
      .map { it.replace("'", "") } // remove any single quotes
      .toList()

    val proc = ProcessBuilder(parts)
      .redirectOutput(ProcessBuilder.Redirect.PIPE)
      .redirectError(ProcessBuilder.Redirect.PIPE)
      .start()

    proc.waitFor(10, TimeUnit.MINUTES)
    proc.inputStream.bufferedReader().readText()
  } catch (e: IOException) {
    e.printStackTrace()
    null
  }
}
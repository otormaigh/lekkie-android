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

package ie.pennylabs.lekkie.plugin.task

import ie.pennylabs.lekkie.plugin.toolbox.extension.runCommand
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

open class GenerateChangelogTask : DefaultTask() {
  init {
    group = "Releasing"
    description = "Generate a changelog based on the range of commits that triggered a build on CircleCI."
  }

  @TaskAction
  fun run() {
    var commitRange = System.getenv("CIRCLE_COMPARE_URL").split("/").last()
    if (commitRange.isEmpty()) System.getenv("CIRCLE_SHA1")
    if (!commitRange.contains("...")) commitRange = "HEAD^..$commitRange"
    logger.info("commitRange -> $commitRange")

    File("app/src/main/play/release-notes/en-GB/internal.txt").apply {
      createNewFile()
      writeText("""git log --pretty=-%s $commitRange""".runCommand() ?: "")
    }
  }
}
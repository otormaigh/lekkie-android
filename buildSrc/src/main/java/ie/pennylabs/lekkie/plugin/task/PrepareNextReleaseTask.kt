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

import ie.pennylabs.lekkie.plugin.toolbox.BuildConst
import ie.pennylabs.lekkie.plugin.toolbox.extension.runCommand
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class PrepareNextReleaseTask : DefaultTask() {
  init {
    group = "Releasing"
    description = "Prepares the project for the next release version, see: RELEASING.md"
  }

  @TaskAction
  fun run() {
    val newVersion = BuildConst.Version.name
      .replaceAfter("-", "")
      .removeSuffix("-")

    // 7. Create a new release branch `git checkout -b release-{versionName}`
    """git checkout -b release-$newVersion""".runCommand()

    // 8. Update previous `CHANGELOG.md` entry to append title with the build commit of that release
    """sed -i '' -e '1 s/$/ - ${BuildConst.Git.shortHash}/' ${project.rootDir}/CHANGELOG.md""".runCommand()

    // 9. Commit changes `git commit -am 'bump version to {versionName}'
    """git commit -am 'bump version to $newVersion'""".runCommand()
  }
}
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

package ie.pennylabs.lekkie.plugin

import ie.pennylabs.lekkie.plugin.task.DetektTask
import ie.pennylabs.lekkie.plugin.task.GenerateChangelogTask
import ie.pennylabs.lekkie.plugin.task.PrepareNextReleaseTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.wrapper.Wrapper
import org.gradle.kotlin.dsl.task

class LekkiePlugin : Plugin<Project> {
  override fun apply(target: Project) {
    target.tasks.register("generateChangelog", GenerateChangelogTask::class.java)
    target.tasks.register("prepareNextRelease", PrepareNextReleaseTask::class.java)
    target.tasks.register("detekt", DetektTask::class.java)


    target.task<Delete>("clean") {
      delete = setOf(target.rootProject.buildDir)
    }

    target.tasks.withType(Wrapper::class.java) {
      gradleVersion = "5.1"
      distributionType = Wrapper.DistributionType.ALL
    }
  }
}
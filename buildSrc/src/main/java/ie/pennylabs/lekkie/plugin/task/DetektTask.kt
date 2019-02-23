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

import org.gradle.api.tasks.JavaExec

open class DetektTask : JavaExec() {
  init {
    project.configurations.create("detekt")
    main = "io.gitlab.arturbosch.detekt.cli.Main"
    classpath = project.configurations.getByName("detekt")

    val input = "${project.rootDir}/app"
    val config = "${project.rootDir}/buildSrc/detekt.yml"
    val filters = ".*Test.*,.*AndroidTest.*,.*/resources/.*,.*/tmp/.*"
    val report = "html:${project.rootDir}/app/build/reports/detekt/detekt.html"
    val params = listOf("-i", input, "-c", config, "-f", filters, "-r", report)
    args(params)

    project.dependencies.add("detekt", "io.gitlab.arturbosch.detekt:detekt-cli:1.0.0-RC14")
  }
}
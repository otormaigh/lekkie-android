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

import dagger.Subcomponent
import dagger.android.AndroidInjector
import ie.pennylabs.lekkie.feature.info.InfoController
import ie.pennylabs.lekkie.feature.list.OutageListController
import ie.pennylabs.lekkie.feature.map.OutageMapController

@Subcomponent
interface OutageListComponent : AndroidInjector<OutageListController> {
  @Subcomponent.Builder
  abstract class Builder : AndroidInjector.Builder<OutageListController>()
}

@Subcomponent
interface OutageMapComponent : AndroidInjector<OutageMapController> {
  @Subcomponent.Builder
  abstract class Builder : AndroidInjector.Builder<OutageMapController>()
}

@Subcomponent
interface InfoComponent : AndroidInjector<InfoController> {
  @Subcomponent.Builder
  abstract class Builder : AndroidInjector.Builder<InfoController>()
}
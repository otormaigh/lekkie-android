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

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule
import ie.pennylabs.lekkie.LekkieApplication
import ie.pennylabs.lekkie.lib.data.di.DataComponent
import javax.inject.Singleton

@Singleton
@Component(
  dependencies = [
    DataComponent::class
  ],
  modules = [
    AndroidInjectionModule::class,
    AndroidSupportInjectionModule::class,
    ActivityBuilder::class,
    FragmentBuilder::class,
    ViewModelModule::class])
interface AppComponent {
  @Component.Builder
  interface Builder {
    @BindsInstance
    fun application(application: Application): Builder

    fun dataComponent(dataComponent: DataComponent): Builder

    fun build(): AppComponent
  }

  fun inject(application: LekkieApplication)
}
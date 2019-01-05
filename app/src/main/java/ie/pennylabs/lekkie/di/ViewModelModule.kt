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

package ie.pennylabs.lekkie.di;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import ie.pennylabs.lekkie.feature.list.OutageListViewModel
import ie.pennylabs.lekkie.feature.map.OutageMapViewModel

@Module
abstract class ViewModelModule {
  @Binds
  abstract fun provideViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory

  @Binds
  @IntoMap
  @ViewModelKey(OutageListViewModel::class)
  abstract fun provideOutageList(viewModel: OutageListViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(OutageMapViewModel::class)
  abstract fun provideOutageMap(viewModel: OutageMapViewModel): ViewModel
}
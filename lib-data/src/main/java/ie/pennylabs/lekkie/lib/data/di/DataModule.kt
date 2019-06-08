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

package ie.pennylabs.lekkie.lib.data.di

import android.app.Application
import android.location.Geocoder
import androidx.room.Room
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import ie.pennylabs.lekkie.lib.data.model.OutageDao
import ie.pennylabs.lekkie.lib.data.moshi.EpochAdapter
import ie.pennylabs.lekkie.lib.data.room.LekkieDatabase
import java.util.*

@Module
object DataModule {
  @Provides
  @JvmStatic
  fun provideMoshi(): Moshi =
    Moshi.Builder()
      .add(EpochAdapter())
      .build()

  @Provides
  @JvmStatic
  fun provideDatabase(context: Application): LekkieDatabase =
    Room.databaseBuilder(context.applicationContext, LekkieDatabase::class.java, LekkieDatabase.NAME)
      .fallbackToDestructiveMigration()
      .build()

  @Provides
  @JvmStatic
  fun provideOutageDao(database: LekkieDatabase): OutageDao = database.outageDao()

  @Provides
  @JvmStatic
  fun provideGeocoder(application: Application): Geocoder = Geocoder(application, Locale.getDefault())
}
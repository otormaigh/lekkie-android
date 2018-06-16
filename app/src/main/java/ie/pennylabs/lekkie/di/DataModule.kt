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

import androidx.room.Room
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import ie.pennylabs.lekkie.LekkieApplication
import ie.pennylabs.lekkie.data.model.OutageDao
import ie.pennylabs.lekkie.data.moshi.EpochAdapter
import ie.pennylabs.lekkie.data.room.LekkieDatabase
import javax.inject.Singleton

@Module
object DataModule {
  @Provides
  @Singleton
  @JvmStatic
  fun provideMoshi(): Moshi =
    Moshi.Builder()
      .add(EpochAdapter())
      .build()

  @Provides
  @Singleton
  @JvmStatic
  fun provideDatabase(context: LekkieApplication): LekkieDatabase =
    Room.databaseBuilder(context.applicationContext, LekkieDatabase::class.java, "lekkie.db")
      .fallbackToDestructiveMigration()
      .build()

  @Provides
  @JvmStatic
  fun provideOutageDao(database: LekkieDatabase): OutageDao = database.outageDao()
}
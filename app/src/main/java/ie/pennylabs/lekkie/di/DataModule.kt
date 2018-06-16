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
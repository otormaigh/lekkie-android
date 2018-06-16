package ie.pennylabs.lekkie.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ie.pennylabs.lekkie.feature.OutageListActivity
import ie.pennylabs.lekkie.feature.map.OutageMapActivity

@Module
interface ActivityBuilder {
  @ContributesAndroidInjector
  fun bindOutageListActivity(): OutageListActivity

  @ContributesAndroidInjector
  fun bindOutageMapActivity(): OutageMapActivity
}
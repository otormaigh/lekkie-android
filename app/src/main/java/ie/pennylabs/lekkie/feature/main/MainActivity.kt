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

package ie.pennylabs.lekkie.feature.main

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import ie.pennylabs.lekkie.R
import ie.pennylabs.lekkie.feature.gdpr.GdprBottomSheet
import ie.pennylabs.lekkie.feature.info.InfoFragment
import ie.pennylabs.lekkie.feature.list.OutageListFragment
import ie.pennylabs.lekkie.feature.map.OutageMapFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
  private var currentFragment: Fragment? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    GdprBottomSheet.show(this)

    bottomNav.setOnNavigationItemSelectedListener(this)
    bottomNav.selectedItemId = R.id.menuList
  }

  override fun onNavigationItemSelected(item: MenuItem): Boolean = replaceFragment(
    when (item.itemId) {
      R.id.menuMap -> OutageMapFragment()
      R.id.menuList -> OutageListFragment()
      R.id.menuInfo -> InfoFragment()
      else -> null
    }
  )

  private fun replaceFragment(fragment: Fragment?): Boolean = when {
    fragment == null -> false
    currentFragment != null && fragment::class.java == currentFragment!!::class.java -> true
    else -> {
      currentFragment = fragment
      supportFragmentManager.beginTransaction()
        .replace(R.id.contentView, fragment, fragment::class.java.canonicalName)
        .commit()

      // So [setOnNavigationItemSelectedListener] doesn't have to supply its own return.
      true
    }
  }
}
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
import androidx.appcompat.app.AppCompatActivity
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import ie.pennylabs.lekkie.R
import ie.pennylabs.lekkie.feature.gdpr.GdprBottomSheet
import ie.pennylabs.lekkie.feature.list.OutageListController
import ie.pennylabs.lekkie.feature.map.OutageMapController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
  private lateinit var router: Router

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    GdprBottomSheet.show(this)

    router = Conductor.attachRouter(this, controllerContainer, savedInstanceState)
    router.setRoot(RouterTransaction.with(OutageListController()))

    bottomNav.selectedItemId = R.id.menuList
    bottomNav.setOnNavigationItemSelectedListener { item ->
      when (item.itemId) {
        R.id.menuMap -> {
          router.setRoot(RouterTransaction.with(OutageMapController().apply {
            retainViewMode = Controller.RetainViewMode.RETAIN_DETACH
          }))
          true
        }
        R.id.menuList -> {
          router.setRoot(RouterTransaction.with(OutageListController()))
          true
        }
        else -> false
      }
    }
  }
}
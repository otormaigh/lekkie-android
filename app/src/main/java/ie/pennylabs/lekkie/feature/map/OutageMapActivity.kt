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

package ie.pennylabs.lekkie.feature.map

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.android.AndroidInjection
import ie.pennylabs.lekkie.R
import ie.pennylabs.lekkie.data.model.OutageDao
import kotlinx.android.synthetic.main.activity_outage_map.*
import javax.inject.Inject

class OutageMapActivity : AppCompatActivity(), OnMapReadyCallback {
  @Inject
  lateinit var outageDao: OutageDao
  private lateinit var map: GoogleMap

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)

    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_outage_map)
    (mapFragment as SupportMapFragment).getMapAsync(this)
  }

  override fun onMapReady(googleMap: GoogleMap) {
    map = googleMap

    val middleish = LatLng(53.304886, -8.009181)
    map.moveCamera(CameraUpdateFactory.newLatLng(middleish))
    map.animateCamera(CameraUpdateFactory.newLatLngZoom(middleish, 7f))

    outageDao.fetchAll().observe(this, Observer { outages ->
      outages?.forEach { outage ->
        val point = LatLng(outage.point.latitude, outage.point.longitude)
        map.addMarker(MarkerOptions().position(point).title(outage.location))
      }
    })
  }
}

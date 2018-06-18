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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.christianbahl.conductor.ConductorInjection
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import ie.pennylabs.lekkie.R
import ie.pennylabs.lekkie.arch.BaseController
import ie.pennylabs.lekkie.data.model.OutageDao
import kotlinx.android.synthetic.main.controller_outage_map.view.*
import javax.inject.Inject

class OutageMapController : BaseController(), OnMapReadyCallback {
  @Inject
  lateinit var outageDao: OutageDao

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View =
    inflater.inflate(R.layout.controller_outage_map, container, false).apply {
      mapView.onCreate(args)
    }

  override fun onAttach(view: View) {
    ConductorInjection.inject(this)

    super.onAttach(view)
    view.mapView.apply {
      onResume()
      getMapAsync(this@OutageMapController)
    }
  }

  override fun onDetach(view: View) {
    super.onDetach(view)
    view.mapView.onPause()
  }

  override fun onDestroyView(view: View) {
    super.onDestroyView(view)
    view.mapView.onDestroy()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    view?.mapView?.onSaveInstanceState(outState)
  }

  override fun onMapReady(map: GoogleMap) {
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
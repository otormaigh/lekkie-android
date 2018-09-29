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

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.Observer
import com.bluelinelabs.conductor.ViewModelController
import com.christianbahl.conductor.ConductorInjection
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import ie.pennylabs.lekkie.R
import ie.pennylabs.lekkie.data.model.Outage
import ie.pennylabs.lekkie.data.model.OutageDao
import kotlinx.android.synthetic.main.controller_outage_map.view.*
import javax.inject.Inject

class OutageMapController : ViewModelController(), OnMapReadyCallback {
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
      val icFault = view?.context?.vectorToBitmap(R.drawable.ic_outage_fault)
      val icPlanned = view?.context?.vectorToBitmap(R.drawable.ic_outage_planned)
      val icUnknown = view?.context?.vectorToBitmap(R.drawable.ic_outage_unkonwn)
      outages?.forEach { outage ->
        val point = LatLng(outage.point.latitude, outage.point.longitude)
        map.addMarker(MarkerOptions()
          .position(point)
          .title(outage.location)
          .icon(when (outage.type) {
            Outage.FAULT -> icFault
            Outage.PLANNED -> icPlanned
            else -> icUnknown
          }))
      }
    })
  }
}

private fun Context.vectorToBitmap(@DrawableRes id: Int, @ColorRes color: Int = R.color.colorPrimaryDark): BitmapDescriptor {
  val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
  val bitmap = Bitmap.createBitmap(vectorDrawable!!.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
  val canvas = Canvas(bitmap)
  vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
  DrawableCompat.setTint(vectorDrawable, ContextCompat.getColor(this, color))
  vectorDrawable.draw(canvas)
  return BitmapDescriptorFactory.fromBitmap(bitmap)
}
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

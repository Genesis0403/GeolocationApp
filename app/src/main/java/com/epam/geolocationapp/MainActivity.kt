package com.epam.geolocationapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Main Activity which contains [LocationFragment].
 * Also requests [Manifest.permission.ACCESS_FINE_LOCATION].
 *
 * @author Vlad Korotkevich
 */

class MainActivity : FragmentActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION
            )
        }

        supportFragmentManager.beginTransaction().apply {
            add(R.id.locationFragmentContainer, LocationFragment.newInstance())
            commit()
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        googleMap.addMarker(MarkerOptions()
            .position(LatLng(53.863591,27.476595))
            .title("Marker"))
    }

    private companion object {
        private const val LOCATION_PERMISSION = 1
    }
}

package com.epam.geolocationapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.IllegalStateException

/**
 * Main Activity which contains [LocationFragment] and [R.id.map].
 * Also requests [Manifest.permission.ACCESS_FINE_LOCATION].
 *
 * @author Vlad Korotkevich
 */

class MainActivity : FragmentActivity(), OnMapReadyCallback, LocationFragment.OnLocationListener {

    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkForPermission()
    }

    private fun checkForPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_PERMISSION -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "App requires location permission", Toast.LENGTH_SHORT).show()
                    throw IllegalStateException("App requires location permission")
                } else {
                    loadMapAndControls()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun loadMapAndControls() {
        supportFragmentManager.beginTransaction().apply {
            add(R.id.locationFragmentContainer, LocationFragment.newInstance())
            commit()
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.isMyLocationEnabled = true
    }

    override fun onInsert(latitude: Double, longitude: Double, radius: Double) {
        map.apply {
            val latLng = LatLng(latitude, longitude)
            addMarker(
                MarkerOptions()
                    .position(latLng)
            )
            addCircle(
                CircleOptions()
                    .center(latLng)
                    .radius(radius)
                    .strokeWidth(STROKE_WIDTH)
            )
        }
    }

    override fun onDelete() {
        map.clear()
    }

    private companion object {
        private const val LOCATION_PERMISSION = 1
        private const val STROKE_WIDTH = 0f
    }
}

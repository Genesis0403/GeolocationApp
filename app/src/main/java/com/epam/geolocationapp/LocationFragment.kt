package com.epam.geolocationapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Fragment which responsible for input of latitude and longitude,
 * starts [GeofenceService] when user setting a point and show it on map.
 *
 * @see GeofenceService
 *
 * @author Vlad Korotkevich
 */

class LocationFragment : Fragment() {

    private lateinit var geofencingClient: GeofencingClient
    private lateinit var locationListener: OnLocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        geofencingClient = LocationServices.getGeofencingClient(context!!)
        locationListener = context as OnLocationListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.location_fragment, container, false)

        view.findViewById<EditText>(R.id.latitudeEditText)
        view.findViewById<EditText>(R.id.longitudeEditText)

        val confirmFab = view.findViewById<FloatingActionButton>(R.id.confirmButton)
        confirmFab.setOnClickListener {
            Log.d(TAG, "CONFIRMING")
            val latitudeEdit = view.findViewById<EditText>(R.id.latitudeEditText)
            val longitudeEdit = view.findViewById<EditText>(R.id.longitudeEditText)
            val latitude = latitudeEdit.text.toString()
            val longitude = longitudeEdit.text.toString()

            if (latitude!= EMPTY_STRING && longitude != EMPTY_STRING) {
                initAcceptClickListener(latitude.toDouble(), longitude.toDouble(), GEOFENCE_RADIUS.toDouble())
            } else {
                Toast.makeText(context, getString(R.string.empty_edits_error), Toast.LENGTH_LONG).show()
            }
        }

        val cancelFab = view.findViewById<FloatingActionButton>(R.id.cancelButton)
        cancelFab.setOnClickListener {
            initCancelClickListener()
        }

        return view
    }

    private fun initAcceptClickListener(latitude: Double, longitude: Double, radius: Double) {
        locationListener.onInsert(latitude, longitude, radius)

        val intent = Intent(context, GeofenceService::class.java).apply {
            putExtra(LATITUDE_EXTRA, latitude)
            putExtra(LONGITUDE_EXTRA, longitude)
            putExtra(RADIUS_EXTRA, radius)
            action = ADD_ACTION
        }
        context?.startService(intent)
    }

    private fun initCancelClickListener() {
        locationListener.onDelete()

        val intent = Intent(context, GeofenceService::class.java).apply {
            action = REMOVE_ACTION
        }
        context?.startService(intent)
    }

    interface OnLocationListener {
        fun onInsert(latitude: Double, longitude: Double, radius: Double)
        fun onDelete()
    }

    companion object {
        private const val TAG = "LOCATION FRAGMENT"
        private const val LATITUDE_EXTRA = "LATITUDE_EXTRA"
        private const val LONGITUDE_EXTRA = "LONGITUDE_EXTRA"
        private const val RADIUS_EXTRA = "RADIUS_EXTRA"
        private const val ADD_ACTION = "ADD_ACTION"
        private const val REMOVE_ACTION = "REMOVE_ACTION"
        private const val EMPTY_STRING = ""

        private const val GEOFENCE_RADIUS = 100f

        fun newInstance() = LocationFragment()
    }
}
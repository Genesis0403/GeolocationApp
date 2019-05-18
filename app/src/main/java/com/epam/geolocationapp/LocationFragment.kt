package com.epam.geolocationapp

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton

class LocationFragment : Fragment() {

    private lateinit var geofencingClient: GeofencingClient

    private val pendingIntent by lazy {
        val intent = Intent(context, GeofenceService::class.java)
        PendingIntent.getService(context, PENDING_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        geofencingClient = LocationServices.getGeofencingClient(context!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.location_fragment, container, false)

        view.findViewById<EditText>(R.id.latitudeEditText).setText("53.863591")
        view.findViewById<EditText>(R.id.longitudeEditText).setText("27.476595")

        val fab = view.findViewById<FloatingActionButton>(R.id.confirmButton)
        fab.setOnClickListener {
            initFabClickListener(view)
        }

        return view
    }

    private fun initFabClickListener(view: View) {
        val latitudeEdit = view.findViewById<EditText>(R.id.latitudeEditText)
        val longitudeEdit = view.findViewById<EditText>(R.id.longitudeEditText)
        val latitude = latitudeEdit.text.toString()
        val longitude = longitudeEdit.text.toString()
        addGeofence(latitude.toDouble(), longitude.toDouble())
    }

    @SuppressLint("MissingPermission")
    private fun addGeofence(latitude: Double, longitude: Double) {
        val request = getGeofenceRequest(
            buildGeofence(
                TEMP_ID,
                latitude,
                longitude,
                GEOFENCE_RADIUS
            )
        )
        geofencingClient.addGeofences(request, pendingIntent).run {
            addOnSuccessListener {
                Log.d(TAG, "SUCCESS")
                Toast.makeText(context, getString(R.string.point_success), Toast.LENGTH_SHORT).show()
            }
            addOnFailureListener {
                Log.d(TAG, "FAILING with $it")
                Toast.makeText(context, getString(R.string.point_failure), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun buildGeofence(
        requestId: String,
        latitude: Double,
        longitude: Double,
        radius: Float
    ) = Geofence.Builder().apply {
        setRequestId(requestId)
        setCircularRegion(latitude, longitude, radius)
        setExpirationDuration(Geofence.NEVER_EXPIRE)
        setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
    }.build()

    private fun getGeofenceRequest(geofence: Geofence): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER)
            addGeofence(geofence)
        }.build()
    }

    companion object {
        private const val TAG = "LOCATION FRAGMENT"
        private const val TEMP_ID = "TEST_GEOFENCE"
        private const val GEOFENCE_RADIUS = 100f
        private const val PENDING_REQUEST_CODE = 0

        fun newInstance() = LocationFragment()
    }
}
package com.epam.geolocationapp

import android.annotation.SuppressLint
import android.app.IntentService
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

/**
 * IntentService which responsible for creation of geofence.
 *
 * @see LocationReceiver
 *
 * @author Vlad Korotkevich
 */

class GeofenceService : IntentService(TAG) {

    private lateinit var geofencingClient: GeofencingClient
    private val pendingIntent by lazy {
        val intent = Intent(this, LocationReceiver::class.java)
        PendingIntent.getBroadcast(this, PENDING_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onCreate() {
        super.onCreate()
        geofencingClient = LocationServices.getGeofencingClient(this)
    }

    override fun onHandleIntent(intent: Intent?) {
        Log.d(TAG, intent?.action?.toString())
        when (intent?.action) {
            ADD_ACTION -> {
                val latitude = intent.getDoubleExtra(LATITUDE_EXTRA, .0)
                val longitude = intent.getDoubleExtra(LONGITUDE_EXTRA, .0)
                val radius = intent.getDoubleExtra(RADIUS_EXTRA, .0)
                addGeofence(latitude, longitude, radius.toFloat())
            }
            REMOVE_ACTION -> {
                geofencingClient.removeGeofences(pendingIntent).run {
                    addOnSuccessListener {
                        initOnSuccessListener(getString(R.string.remove_success))
                    }
                    addOnFailureListener {
                        initOnFailureListener(getString(R.string.remove_error), it)
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun addGeofence(latitude: Double, longitude: Double, radius: Float) {
        val request = getGeofenceRequest(
            buildGeofence(
                TEMP_ID,
                latitude,
                longitude,
                radius
            )
        )
        geofencingClient.addGeofences(request, pendingIntent).run {
            addOnSuccessListener {
                initOnSuccessListener(getString(R.string.point_success))
            }
            addOnFailureListener {
                initOnFailureListener(getString(R.string.point_failure), it)
            }
        }
    }

    private fun initOnSuccessListener(message: String) {
        Log.d(TAG, "SUCCESS")
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun initOnFailureListener(message: String, exception: Exception) {
        Log.e(TAG, "$message $exception")
        Toast.makeText(applicationContext, getString(R.string.point_failure), Toast.LENGTH_SHORT).show()
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
        setNotificationResponsiveness(FIVE_MINUTES_IN_MILLIS)
    }.build()

    private fun getGeofenceRequest(geofence: Geofence): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER)
            addGeofence(geofence)
        }.build()
    }

    private companion object {
        private const val TAG = "GEOFENCE SERVICE"
        private const val FIVE_MINUTES_IN_MILLIS = 300000

        private const val TEMP_ID = "TEST_GEOFENCE"

        private const val PENDING_REQUEST_CODE = 0
        private const val LATITUDE_EXTRA = "LATITUDE_EXTRA"
        private const val LONGITUDE_EXTRA = "LONGITUDE_EXTRA"
        private const val RADIUS_EXTRA = "RADIUS_EXTRA"

        private const val ADD_ACTION = "ADD_ACTION"
        private const val REMOVE_ACTION = "REMOVE_ACTION"
    }
}
package com.epam.geolocationapp

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import java.lang.Exception

class GeofenceService : Service() {

    private lateinit var geofencingClient: GeofencingClient
    private val pendingIntent by lazy {
        val intent = Intent(this, LocationReceiver::class.java)
        PendingIntent.getBroadcast(this, PENDING_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onCreate() {
        super.onCreate()
        geofencingClient = LocationServices.getGeofencingClient(this)
        createNotificationChannel(FOREGROUND_CHANNEL)
        startForeground(
            FOREGROUND_ID,
            createForegroundNotification("Location App", "Working in background...", FOREGROUND_CHANNEL)
        )
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, intent?.action?.toString())
        when (intent?.action) {
            ADD_ACTION -> {
                val latitude = intent.getDoubleExtra(LATITUDE_EXTRA, .0)
                val longitude = intent.getDoubleExtra(LONGITUDE_EXTRA, .0)
                addGeofence(latitude, longitude)
            }
            REMOVE_ACTION -> {
                geofencingClient.removeGeofences(pendingIntent).run {
                    addOnSuccessListener {
                        initOnSuccessListener(getString(R.string.remove_success))
                    }
                    addOnFailureListener {
                        initOnFailureListener(getString(R.string.remove_error), it)
                    }
                    stopSelf()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
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
        Log.e(TAG, "FAILING with $exception")
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
    }.build()

    private fun getGeofenceRequest(geofence: Geofence): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER)
            addGeofence(geofence)
        }.build()
    }

    private fun createForegroundNotification(
        title: String,
        content: String,
        channelId: String
    ): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pIntent = PendingIntent.getActivity(this, 3, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        return NotificationCompat.Builder(this, channelId).apply {
            setContentTitle(title)
            setContentText(content)
            addAction(R.drawable.ic_location_on_black_24dp, getString(R.string.to_the_app_reply), pIntent)
            setSmallIcon(R.drawable.ic_location_on_black_24dp)
            priority = NotificationCompat.PRIORITY_DEFAULT
        }.build()
    }


    private fun createNotificationChannel(channelId: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(
            NotificationChannel(
                channelId,
                getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = getString(R.string.channel_description)
            })
    }

    private companion object {
        private const val TAG = "GEOFENCE SERVICE"


        private const val FOREGROUND_CHANNEL = "FOREGROUND_CHANNEL"
        private const val FOREGROUND_ID = 2

        private const val TEMP_ID = "TEST_GEOFENCE"
        private const val GEOFENCE_RADIUS = 100f

        private const val PENDING_REQUEST_CODE = 0
        private const val LATITUDE_EXTRA = "LATITUDE_EXTRA"
        private const val LONGITUDE_EXTRA = "LONGITUDE_EXTRA"

        private const val ADD_ACTION = "ADD_ACTION"
        private const val REMOVE_ACTION = "REMOVE_ACTION"
    }

}
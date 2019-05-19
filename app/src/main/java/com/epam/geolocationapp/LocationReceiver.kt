package com.epam.geolocationapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

/**
 * Receiver which gets intents from [GeofenceService]
 * when user reaches a certain point.
 *
 * @see GeofenceService
 *
 * @author Vlad Korotkevich
 */

class LocationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "WE ARE IN RECEIVER")
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            Log.d(TAG, "Event has error")
            return
        }

        val geofenceTransition = geofencingEvent.geofenceTransition

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            val location = geofencingEvent.triggeringLocation
            context?.let {
                createNotificationChannel(it, LOCATION_CHANNEL)

                with(NotificationManagerCompat.from(it)) {
                    notify(LOCATION_ID, createLocationNotification(it, location, LOCATION_CHANNEL))
                }
            }
        }
    }

    private fun createLocationNotification(context:Context, location: Location, channelId: String) =
        NotificationCompat.Builder(context, channelId).apply {
            setContentTitle(context.getString(R.string.notification_title))
            setContentText("You've reached ${location.latitude} ${location.longitude}")
            setSmallIcon(R.drawable.ic_location_on_black_24dp)
            setCategory(NotificationCompat.CATEGORY_ALARM)
            priority = NotificationCompat.PRIORITY_DEFAULT
        }.build()

    private fun createNotificationChannel(context: Context, channelId: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(
            NotificationChannel(
                channelId,
                context.getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.channel_description)
            })
    }
    private companion object {
        private const val TAG = "LOCATION RECEIVER"
        private const val LOCATION_CHANNEL = "LOCATION_CHANNEL"
        private const val LOCATION_ID = 1
    }
}
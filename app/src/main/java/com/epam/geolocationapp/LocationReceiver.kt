package com.epam.geolocationapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import androidx.core.app.NotificationCompat

class LocationReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {

    }

    private fun createLocationNotification(context:Context, location: Location, channelId: String) =
        NotificationCompat.Builder(context, channelId).apply {
            setContentTitle(context.getString(R.string.notification_title))
            setContentText("You've reached ${location.latitude} ${location.longitude}")
            setSmallIcon(R.drawable.ic_location_on_black_24dp)
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
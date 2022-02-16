package com.mint.minttracker.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.mint.minttracker.MainActivity
import com.mint.minttracker.R
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import com.mint.minttracker.App
import javax.inject.Inject

class LocationServiceForeground : Service() {

    @Inject
    lateinit var tracker: Tracker

    private var serviceStarted = false

    override fun onCreate() {
        super.onCreate()
        App.instance.appComponent.injectLocationServiceForeground(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val status = intent.getStringExtra(STATUS)
            if (status != null) {
                when (intent.action) {
                    ACTION_START_FOREGROUND_SERVICE -> startForegroundService(status)
                    ACTION_STOP_FOREGROUND_SERVICE -> stopForegroundService(status)
                    ACTION_PLAY -> Toast.makeText(applicationContext, "You click Play button.", Toast.LENGTH_LONG).show()
                    ACTION_PAUSE -> Toast.makeText(applicationContext, "You click Pause button.", Toast.LENGTH_LONG).show()
                }
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun startForegroundService(status: String) {
        startForeground(1, createNotification().build())
        tracker.start(status)
        serviceStarted = true
    }

    private fun createNotification(): NotificationCompat.Builder {
        createNotificationChannel()
        val pendingIntent = PendingIntent.getActivity(this, 0, Intent(), 0)
        // Create notification builder.
        val bigTextStyle = NotificationCompat.BigTextStyle()
            .setBigContentTitle("MintTracker implemented by foreground service.")
            .bigText("Android foreground service is a android service which can run in foreground always, it can be controlled by user via notification.");

        val notification = NotificationCompat.Builder(this, FOREGROUND_SERVICE_CHANNEL)
            .setStyle(bigTextStyle)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_outline_gps_not_fixed_24)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_outline_gps_not_fixed_24))
            .setFullScreenIntent(pendingIntent, true)

        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.addFlags(FLAG_ACTIVITY_NEW_TASK)
        notificationIntent.addFlags(FLAG_ACTIVITY_SINGLE_TOP)
        val contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        notification.setContentIntent(contentIntent)

        // Add Play button intent in notification.
        val playIntent = Intent(this, LocationServiceForeground::class.java)
        playIntent.action = ACTION_PLAY
        val pendingPlayIntent = PendingIntent.getService(this, 0, playIntent, 0)
        notification.addAction(NotificationCompat.Action(android.R.drawable.ic_media_play, "Play", pendingPlayIntent))

        // Add Pause button intent in notification.
        val pauseIntent = Intent(this, LocationServiceForeground::class.java)
        pauseIntent.action = ACTION_PAUSE
        val pendingPrevIntent = PendingIntent.getService(this, 0, pauseIntent, 0)
        notification.addAction(NotificationCompat.Action(android.R.drawable.ic_media_pause, "Pause", pendingPrevIntent))

        return notification
    }

    private fun stopForegroundService(status: String) {
        tracker.stop(status)
        if (serviceStarted){
            serviceStarted = false
            stopForeground(true)
            stopSelf(1)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                FOREGROUND_SERVICE_CHANNEL,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    companion object {
        const val ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE"
        const val ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_PLAY = "ACTION_PLAY"
        const val STATUS = "STATUS"
        const val FOREGROUND_SERVICE_CHANNEL = "FOREGROUND_SERVICE_CHANNEL"
    }

    override fun onDestroy() {
        tracker.onDestroy()
        super.onDestroy()
    }
}


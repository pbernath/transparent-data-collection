package com.thesisproject.thesis_vt25.data.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Process
import android.util.Log
import androidx.core.app.NotificationCompat

class ContractService : Service() {
    private lateinit var handlerThread: HandlerThread
    private lateinit var serviceHandler: Handler
    private var isRunning = false

    override fun onCreate() {
        super.onCreate()

        // Start a background thread
        handlerThread = HandlerThread("ContractServiceThread", Process.THREAD_PRIORITY_BACKGROUND)
        handlerThread.start()

        serviceHandler = Handler(handlerThread.looper)
        createNotificationChannel()
        // Start foreground notification
        val notification = NotificationCompat.Builder(this, "service_channel")
            .setContentTitle("Contract Service Running")
            .setContentText("Running until stopped manually.")
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .build()

        startForeground(1, notification)
        Log.d("Service", "Foreground service started")
    }

    private fun startRepeatingTask(context: Context) {
        if (isRunning) return
        isRunning = true

        serviceHandler.post(object : Runnable {
            override fun run() {
                serviceHandler.postDelayed(this, 15000)
                Log.d("Service", "15 sec has passed")
                var notifString = "Browsing for contracts..."
                updateNotification(notifString, context)
            }
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Start repeating task
        startRepeatingTask(this)
        return START_STICKY
    }

    private fun updateNotification(notifString: String, context: Context){
        val notification = NotificationCompat.Builder(context, "service_channel")
            .setContentTitle("Contract Service Running")
            .setContentText(notifString)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .build()
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, notification)
    }
    override fun onDestroy() {
        serviceHandler.removeCallbacksAndMessages(null)
        handlerThread.quitSafely()
        Log.d("Service", "Service destroyed and thread stopped")
        super.onDestroy()
    }

    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "service_channel",
                "Contract Service",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

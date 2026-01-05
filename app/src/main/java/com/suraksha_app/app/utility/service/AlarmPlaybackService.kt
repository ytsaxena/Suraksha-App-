package com.suraksha_app.app.utility.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.suraksha_app.app.R
import com.suraksha_app.app.utility.Constants.ALARM_PLAY_BACK_DURATION
import com.suraksha_app.app.utility.Constants.CHANNEL_ID

class AlarmPlaybackService : Service() {

    private lateinit var mediaPlayer: MediaPlayer


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer.create(this, R.raw.sos_tone)
        mediaPlayer.isLooping = true
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("🔔 AlarmPlaybackService → onStartCommand() called")
        println("➡️ Intent: $intent  | Flags: $flags  | StartId: $startId")

        val notification = createNotification()
        println("📌 Notification created")

        startForeground(1, notification)
        println("🚀 Service moved to FOREGROUND")

        startMusic()
        println("🎵 Music started")

        Handler(mainLooper).postDelayed({
            println("⏳ Handler triggered → stopping music")
            stopMusic()
            println("🛑 Music stopped")
        }, ALARM_PLAY_BACK_DURATION)

        println("⏱ ALARM_PLAY_BACK_DURATION = $ALARM_PLAY_BACK_DURATION ms")
        println("🔁 Returning START_STICKY")

        return START_STICKY
    }

    private fun createNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        val notificationBuilder =
            NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("Emergency SOS")
                .setContentText("Playing SOS").setSmallIcon(R.drawable.ic_filled_sos)
        return notificationBuilder.build()
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID, "Alarm Service Channel", NotificationManager.IMPORTANCE_HIGH
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
    }


    private fun startMusic() {
        println("🎵 startMusic() called")

        if (!this::mediaPlayer.isInitialized) {
            println("❌ MediaPlayer is NOT initialized!")
            return
        }

        if (!mediaPlayer.isPlaying) {
            println("▶️ MediaPlayer is NOT playing → Starting playback")
            mediaPlayer.start()
            println("🎶 Music playback started")
        } else {
            println("⏭ MediaPlayer is already playing → Ignoring start()")
        }
    }

    private fun stopMusic() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

}
package com.suraksha.app

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.suraksha.app.presentation.navigaiton.App
import com.suraksha.app.presentation.theme.SurakshainTheme
import com.suraksha.app.utility.service.AlarmPlaybackService
import dagger.hilt.android.AndroidEntryPoint
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapLibre.getInstance(
            this,
            "unused",
            WellKnownTileServer.MapLibre
        )
        enableEdgeToEdge()
        setContent {
            SurakshainTheme {
                App()
            }
        }
    }

    fun startMyService() {

        val serviceIntent = Intent(this, AlarmPlaybackService::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }

    }

}

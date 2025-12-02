package com.suraksha.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.suraksha.app.presentation.navigaiton.App
import com.suraksha.app.presentation.theme.SurakshainTheme
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
}

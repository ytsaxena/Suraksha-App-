package com.suraksha.app.presentation.navigaiton

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.suraksha.app.R
import com.suraksha.app.presentation.helpline.HelplineScreen
import com.suraksha.app.presentation.map.MapScreen
import com.suraksha.app.presentation.sos.SOSScreen

enum class Destination (
    val route: String,
    val label: String,
    val icon: Int,
    val contentDescription: String
){
    MAP("map", "Map", R.drawable.ic_map, "Map"),
    SOS("sos", "SOS", R.drawable.ic_sos, "SOS"),
    HELPLINE("helpline", "Helpline", R.drawable.ic_helpline, "Helpline")
}

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    startDestination: Destination,
    navHostController: NavHostController
) {
    NavHost(
        navHostController,
        startDestination = startDestination.route
    ){
        Destination.entries.forEach { destination->
            composable(destination.route){
                when(destination){
                    Destination.MAP -> MapScreen()
                    Destination.SOS -> SOSScreen()
                    Destination.HELPLINE -> HelplineScreen()
                }
            }
        }
    }
}
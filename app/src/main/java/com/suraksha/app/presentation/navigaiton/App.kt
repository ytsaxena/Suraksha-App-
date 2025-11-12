package com.suraksha.app.presentation.navigaiton

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.suraksha.app.presentation.theme.Purple40

@Composable
fun App(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val startDestination = Destination.MAP
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    BackHandler(enabled = currentRoute != startDestination.route) {
        navController.navigate(startDestination.route) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                windowInsets = NavigationBarDefaults.windowInsets
            ) {
                Destination.entries
                    .forEachIndexed { index, destination ->
                    val selected = currentRoute == destination.route
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
//                            navController.navigate(destination.route) {
//                                popUpTo(0) { inclusive = true }
//                                launchSingleTop = true
//                            }

                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }

                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = destination.icon!!),
                                contentDescription = destination.contentDescription
                            )
                        },
                        label = {
                            Text(destination.contentDescription)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Purple40,
                            selectedTextColor = Purple40,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { contentPadding ->
        AppNavHost(
            navHostController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(contentPadding)
        )
    }
}
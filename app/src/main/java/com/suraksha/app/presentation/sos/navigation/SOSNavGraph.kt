package com.suraksha.app.presentation.sos.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.suraksha.app.presentation.sos.SOSScreen
import com.suraksha.app.presentation.sos.SOSScreenVM
import com.suraksha.app.presentation.sos.SelectContactScreen

@Composable
fun SOSNavGraph(
    modifier: Modifier = Modifier,
    viewModel: SOSScreenVM = hiltViewModel()
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "sos_main",
        modifier = modifier
    ) {
        composable("sos_main") {
            SOSScreen(
                viewModel = viewModel,
                onSelectContactClicked = {
                    navController.navigate("sos_select_contact")
                }
            )
        }

        composable("sos_select_contact") {
            SelectContactScreen(
                contacts = viewModel.contacts,
                onSelectContactClicked = {
                    navController.popBackStack()
                },
                onBackClicked = {
                    navController.popBackStack()
                }
            )
        }
    }
}
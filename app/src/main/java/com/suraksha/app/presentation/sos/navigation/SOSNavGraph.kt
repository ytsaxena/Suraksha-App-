package com.suraksha.app.presentation.sos.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.suraksha.app.presentation.sos.SOSScreen
import com.suraksha.app.presentation.sos.SelectContactScreen
import com.suraksha.app.presentation.sos.SelectContactVM

@Composable
fun SOSNavGraph(
    modifier: Modifier = Modifier,
    viewModel: SelectContactVM = hiltViewModel()
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "sos_main",
        modifier = modifier
    ) {
        composable("sos_main") {
            SOSScreen(
                onSelectContactClicked = {
                    navController.navigate("sos_select_contact")
                }
            )
        }

        composable("sos_select_contact") {
            SelectContactScreen(
                viewModel = viewModel,
//                onSaveSelectContactClicked = {
//                    navController.popBackStack()
//                },
                onBackClicked = {
                    navController.popBackStack()
                }
            )
        }
    }
}
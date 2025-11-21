package com.suraksha.app.presentation.sos.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.suraksha.app.presentation.sos.intro.SOSIntroScreen
import com.suraksha.app.presentation.sos.intro.SOSIntroScreenVM
import com.suraksha.app.presentation.sos.selectContact.SelectContactScreen
import com.suraksha.app.presentation.sos.selectContact.SelectContactVM
import com.suraksha.app.presentation.sos.sos.SOSScreen

@Composable
fun SOSNavGraph(
    modifier: Modifier = Modifier,
    viewModel1: SelectContactVM = hiltViewModel(),
    viewModel2: SOSIntroScreenVM = hiltViewModel()
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "sos_intro",
        modifier = modifier
    ) {
        composable("sos_intro") {
            SOSIntroScreen(
                viewModel = viewModel2,
                navigateToSOSScreen = {
                    navController.navigate("sos"){
                        popUpTo(0) { inclusive = true }
                    }
                },
                onSelectContactClicked = {
                    navController.navigate("sos_select_contact")
                }
            )
        }

        composable("sos_select_contact") {
            SelectContactScreen(
                viewModel = viewModel1,
                onBackClicked = {
                    navController.popBackStack()
                },
                navigateToSOSScreen = {
                    navController.navigate("sos"){
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable ("sos"){
            SOSScreen()
        }
    }
}
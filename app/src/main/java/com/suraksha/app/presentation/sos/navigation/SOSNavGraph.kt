package com.suraksha.app.presentation.sos.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.suraksha.app.domain.model.Contact
import com.suraksha.app.presentation.sos.intro.SOSIntroScreen
import com.suraksha.app.presentation.sos.intro.SOSIntroScreenVM
import com.suraksha.app.presentation.sos.selectContact.SelectContactScreen
import com.suraksha.app.presentation.sos.selectContact.SelectContactVM
import com.suraksha.app.presentation.sos.sos.SOSScreen

@Composable
fun SOSNavGraph(
    modifier: Modifier = Modifier,
    viewModel1: SelectContactVM = hiltViewModel(),
    viewModel2: SOSIntroScreenVM = hiltViewModel(),
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "sos_intro",
        modifier = modifier
    ) {
        composable("sos_intro") {
            SOSIntroScreen(
                modifier = modifier,
                viewModel = viewModel2,
                navigateToSOSScreen = { contactList ->
                    val contactListStr = Gson().toJson(contactList)
                    navController.navigate("sos/$contactListStr") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onSelectContactClicked = {
                    val contactListStr = Gson().toJson(emptyList<Contact>())
                    navController.navigate("sos_select_contact/${contactListStr}")
                }
            )
        }

        composable("sos_select_contact/{contactList}") { backStackEntry ->
            val contactListStr = backStackEntry.arguments?.getString("contactList")
            val token = object : TypeToken<List<Contact>>() {}.type
            val contactList: List<Contact>? = Gson().fromJson(contactListStr, token)
            SelectContactScreen(
                viewModel = viewModel1,
                onBackClicked = {
                    navController.popBackStack()
                },
                navigateToSOSScreen = { contactList ->
                    val contactListStr = Gson().toJson(contactList)
                    navController.navigate("sos/$contactListStr") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                // selectedContacts = contactList?.toSet() ?: emptySet()
            )
        }

        composable("sos/{contactList}") { backStackEntry ->
            val contactListStr = backStackEntry.arguments?.getString("contactList")
            val token = object : TypeToken<List<Contact>>() {}.type
            val contactList: List<Contact> = Gson().fromJson(contactListStr, token)
            SOSScreen(contactList = contactList, navigateToContactSelectScreen = { contactList ->
                val contactListStr = Gson().toJson(contactList)
                navController.navigate("sos_select_contact/$contactListStr")
            })
        }
    }
}
package com.suraksha.app.presentation.sos.intro

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.suraksha.app.R
import com.suraksha.app.domain.model.Contact
import com.suraksha.app.presentation.sos.components.DescriptionCards
import com.suraksha.app.presentation.theme.White
import com.suraksha.app.presentation.theme.alertColor
import com.suraksha.app.presentation.theme.buttonColorEnd
import com.suraksha.app.presentation.theme.buttonColorStart
import com.suraksha.app.presentation.theme.cameraColor
import com.suraksha.app.presentation.theme.colorGrayBold
import com.suraksha.app.presentation.theme.colorGrayLight
import com.suraksha.app.presentation.theme.smsColor
import com.suraksha.app.presentation.theme.whatsappColor

@Composable
fun SOSIntroScreen(
    viewModel: SOSIntroScreenVM = hiltViewModel(),
    navigateToSOSScreen: (List<Contact>) -> Unit = {},
    onSelectContactClicked: () -> Unit = {},
    modifier: Modifier= Modifier,
) {
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is SOSIntroNavEvent.NavigateToSOSScreen -> navigateToSOSScreen(event.contactList)
            }
        }
    }

    val isChecking by viewModel.isChecking.collectAsState()

    if (isChecking) {
        Box(
            modifier = modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
//        CircularProgressIndicator()
    }

    Column (
        modifier = Modifier
//            .systemBarsPadding()
            .fillMaxSize()
            .padding(top = 32.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Box (
            modifier = Modifier
                .size(100.dp)
                .background(alertColor.copy(0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ){
            Icon(
                painter = painterResource(R.drawable.ic_contact),
                contentDescription = "Add emergency contact",
                tint = alertColor
            )
        }
        Text(
            text = stringResource(R.string.add_emergency_contact),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = colorGrayBold,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = stringResource(R.string.add_emergency_contact_description),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = colorGrayLight,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, end = 32.dp)
        )
        Spacer(Modifier.height(16.dp))
        DescriptionCards(
            title = stringResource(R.string.instant_sms_alert),
            description = stringResource(R.string.instant_sms_alert_description),
            icon = painterResource(R.drawable.ic_sms),
            tint = smsColor
        )
        DescriptionCards(
            title = stringResource(R.string.auto_photo_capture),
            description = stringResource(R.string.auto_photo_capture_description),
            icon = painterResource(R.drawable.ic_camera),
            tint = cameraColor
        )
        DescriptionCards(
            title = stringResource(R.string.whatsapp_integration),
            description = stringResource(R.string.whataspp_integration_description),
            icon = painterResource(R.drawable.ic_whatsapp),
            tint = whatsappColor
        )
        Button(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 20.dp)
                .fillMaxWidth(),
            onClick = { onSelectContactClicked() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            shape = RoundedCornerShape(4.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                buttonColorStart,
                                buttonColorEnd
                            )
                        ),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.select_contact),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview
@Composable
private fun SOSScreenPrev() {
    val navController = rememberNavController()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        SOSIntroScreen()
    }
}
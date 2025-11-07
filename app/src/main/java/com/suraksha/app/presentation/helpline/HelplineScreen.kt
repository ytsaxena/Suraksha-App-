package com.suraksha.app.presentation.helpline

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suraksha.app.presentation.theme.colorGrayBold
import com.suraksha.app.presentation.theme.colorGrayLight
import com.suraksha.app.presentation.theme.poppinsBold
import com.suraksha.app.presentation.theme.poppinsRegular
import com.suraksha.app.ui.theme.HelplineCard
import com.suraksha.app.ui.theme.SafetyTipCard

data class Helpline(
    val title: String, val phone: String
)


@Composable
fun HelplineScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val helplines = listOf(
            Helpline("Women Helpline", "+1091"),
            Helpline("Police Helpline", "+100"),
            Helpline("NGO Support", "+1800123456"),
            Helpline("Domestic Violence", "+181")
        )

        Scaffold(
            bottomBar = {
                SafetyTipCard(
                    """Always inform someone about your whereabouts when 
                | traveling alone.""".trimMargin()
                )
            }) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Emergency Helplines",
                    fontWeight = FontWeight.Bold,
                    fontFamily = poppinsBold,
                    fontSize = 16.sp,
                    color = colorGrayBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 24.dp)
                )

                Text(
                    text = """Verified government & NGO helpline 
                    | numbers.""".trimMargin(),
                    fontSize = 16.sp,
                    color = colorGrayLight,
                    fontFamily = poppinsRegular,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp, top = 14.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 70.dp, top = 12.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(helplines) { helpline ->
                        HelplineCard(title = helpline.title, phone = helpline.phone, onCallClick = {

                        }, onWebsiteClick = {

                        }, onCopyClick = {

                        })
                    }
                }
            }
        }


    }
}
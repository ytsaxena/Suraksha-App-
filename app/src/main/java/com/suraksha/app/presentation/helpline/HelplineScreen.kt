package com.suraksha.app.presentation.helpline

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.suraksha.app.utility.copyToClipboard
import com.suraksha.app.utility.dialNumber
import com.suraksha.app.utility.loadHelplines
import com.suraksha.app.utility.openWebsite
import com.suraksha.app.utility.openWhatsApp

data class Helpline(
    val name: String,
    val number: String,
    val email: String,
    val website: String,
    val isWhatsApp: Boolean
)


@Composable
fun HelplineScreen(modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val helplines = remember { loadHelplines(context) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                        HelplineCard(title = helpline.name, phone = helpline.number, onCallClick = {
                            if (helpline.number.isNotEmpty()) {
                                if (helpline.isWhatsApp) {
                                    openWhatsApp(context, helpline.number)
                                } else {
                                    dialNumber(context, helpline.number)
                                }
                            }
                        }, onWebsiteClick = {
                            if (helpline.website.isNotEmpty()) {
                                openWebsite(context, helpline.website)
                            }
                        }, onCopyClick = {
                            copyToClipboard(context, helpline.number)
                        })
                    }
                }
            }
        }


    }
}
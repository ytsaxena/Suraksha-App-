package com.suraksha.app.presentation.helpline

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suraksha.app.presentation.theme.colorGrayBold
import com.suraksha.app.presentation.theme.colorGrayLight
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

        Scaffold(
            contentWindowInsets = WindowInsets(left = 5.dp , right = 5.dp , bottom = 5.dp),
        ) {
            padding ->
            LazyColumn (
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {

                stickyHeader {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .background(Color.White)
                            .fillMaxWidth()
                            .padding(top = 50.dp, bottom = 20.dp , start = 12.dp, end = 12.dp)
                    ) {
                        Text(
                            text = "Emergency Helplines",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = colorGrayBold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Verified government & NGO helpline numbers.",
                            fontSize = 16.sp,
                            color = colorGrayLight,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                items(helplines) { helpline ->
                        HelplineCard(title = helpline.name, phone = helpline.number, website = helpline.website, onCallClick = {
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
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    SafetyTipCard("Always alert a trusted person whenever you travel alone.")
                    Spacer(modifier = Modifier.height(40.dp))
               }
        }
    }

}

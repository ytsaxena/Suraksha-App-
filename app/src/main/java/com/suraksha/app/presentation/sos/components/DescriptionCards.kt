package com.suraksha.app.presentation.sos.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suraksha.app.R
import com.suraksha.app.presentation.theme.alertColor
import com.suraksha.app.presentation.theme.colorGrayBold
import com.suraksha.app.presentation.theme.colorGrayLight
import com.suraksha.app.presentation.theme.poppinsBold
import com.suraksha.app.presentation.theme.poppinsRegular
import com.suraksha.app.presentation.theme.smsColor
import com.suraksha.app.ui.theme.CircleIconButton

@Composable
fun DescriptionCards(
    title: String,
    description: String,
    icon: Painter,
    tint: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(22.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(35.dp)
                    .background(tint.copy(0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = icon,
                    contentDescription = title,
                    tint = tint,
                )
            }
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = colorGrayBold,
                    fontFamily = poppinsBold,
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = colorGrayLight,
                    fontFamily = poppinsRegular,
                )
            }
        }
    }
}

@Preview
@Composable
private fun DescriptionCardsPrev() {
    DescriptionCards(
        title = "Title",
        description = "description",
        icon = painterResource(R.drawable.ic_sms),
        tint = smsColor
    )
}
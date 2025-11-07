package com.suraksha.app.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun SafetyTipCard(message: String) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .padding(bottom = 8.dp)
            .background(safetyCardBackgroundColor, shape),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Info",
            tint = White,
            modifier = Modifier.size(20.dp)
        )
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ){
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Safety Tip",
                color = White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = poppinsBold,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = message,
                color = White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = poppinsRegular,
            )
        }
    }
}

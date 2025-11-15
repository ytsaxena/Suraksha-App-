package com.suraksha.app.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suraksha.app.presentation.theme.White
import com.suraksha.app.presentation.theme.poppinsBold
import com.suraksha.app.presentation.theme.poppinsRegular
import com.suraksha.app.presentation.theme.safetyCardBackgroundColor


@Composable
fun SafetyTipCard(message: String) {
    val shape = RoundedCornerShape(15.dp)
    Row(
        modifier = Modifier.padding(start = 15.dp, end = 15.dp)
            .fillMaxWidth()
            .background(safetyCardBackgroundColor, shape),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(20.dp))
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Info",
            tint = White,
            modifier = Modifier.size(25.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            modifier = Modifier.weight(1f).padding(vertical = 10.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ){
            Text(
                text = "Safety Tip",
                color = White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = message,
                color = White,
                fontSize = 10.sp,
                lineHeight = 13.sp,
                fontWeight = FontWeight.Normal,
            )
        }
    }
}

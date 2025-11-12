package com.suraksha.app.presentation.sos.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suraksha.app.R
import com.suraksha.app.presentation.theme.colorGrayBold
import com.suraksha.app.presentation.theme.colorGrayLight
import com.suraksha.app.presentation.theme.poppinsBold
import com.suraksha.app.presentation.theme.poppinsRegular

@Composable
fun ContactCard(
    modifier: Modifier = Modifier,
    name: String,
    isSelected: Boolean,
    onSelectChange: (Boolean) -> Unit,
    phoneNumber: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelectChange(!isSelected) }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(35.dp)
                .background(Color.Blue, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            val initials = name
                .split(" ")
                .filter { it.isNotEmpty() }
                .take(2)
                .joinToString("") { it.first().uppercase() }
            Text(
                text = initials,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 14.sp,
                fontFamily = poppinsBold
            )
        }
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = colorGrayBold,
                fontFamily = poppinsBold,
            )
            Text(
                text = phoneNumber,
                fontSize = 12.sp,
                color = colorGrayLight,
                fontFamily = poppinsRegular,
            )
        }
        IconButton(onClick = { onSelectChange(!isSelected) }) {
            Icon(
                painter = if (isSelected)
                    painterResource(R.drawable.ic_checked)
                else
                    painterResource(R.drawable.ic_unchecked),
                contentDescription = if (isSelected) "Selected" else "Not selected",
                tint = if (isSelected) Color(0xFF7C4DFF) else colorGrayLight,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
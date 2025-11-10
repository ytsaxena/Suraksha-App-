package com.suraksha.app.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.suraksha.app.R
import com.suraksha.app.presentation.theme.colorGrayBold
import com.suraksha.app.presentation.theme.colorGrayLight
import com.suraksha.app.presentation.theme.poppinsBold
import com.suraksha.app.presentation.theme.poppinsRegular

@Composable
fun HelplineCard(
    title: String,
    phone: String,
    onCallClick: () -> Unit,
    onWebsiteClick: () -> Unit,
    onCopyClick: () -> Unit
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = colorGrayBold,
                    fontFamily = poppinsBold,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = phone,
                    fontSize = 16.sp,
                    color = colorGrayLight,
                    fontFamily = poppinsRegular,
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CircleIconButton(
                    icon = Icons.Default.Call,
                    contentDescription = "Call",
                    backgroundColor = Color(0x334CAF50),
                    iconTint = Color(0xFF4CAF50),
                    onClick = onCallClick
                )

                CircleIconButton(
                    drawableRes = R.drawable.mage_globe_fill,
                    contentDescription = "Website",
                    backgroundColor = Color(0x333F51B5),
                    onClick = onWebsiteClick
                )


                CircleIconButton(
                    drawableRes = R.drawable.solar_copy_bold,
                    contentDescription = "Copy",
                    backgroundColor = Color(0x33AAAAAA),
                    onClick = onCopyClick
                )
            }
        }
    }
}

@Composable
fun CircleIconButton(
    icon: ImageVector,
    contentDescription: String,
    backgroundColor: Color,
    iconTint: Color = Color.White,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(backgroundColor, shape = CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun CircleIconButton(
    drawableRes: Int,
    contentDescription: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(backgroundColor, shape = CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = drawableRes),
            contentDescription = contentDescription,
            modifier = Modifier.size(20.dp)
        )
    }
}

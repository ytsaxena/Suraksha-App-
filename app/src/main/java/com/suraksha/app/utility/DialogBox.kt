package com.suraksha.app.utility

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.suraksha.app.R
import com.suraksha.app.presentation.theme.White
import com.suraksha.app.presentation.theme.colorGrayBold
import com.suraksha.app.presentation.theme.colorGrayLight
import com.suraksha.app.presentation.theme.negativeColor
import com.suraksha.app.presentation.theme.positiveColor

@Composable
fun DialogBox(
    onDismiss: () -> Unit = {},
    title: String,
    message: String? = null,
    content: @Composable () -> Unit = {}
) {
    Dialog(
        onDismissRequest = { onDismiss() },
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = colorGrayBold
                )
                Spacer(modifier = Modifier.height(12.dp))
                if(message != null){
                    Text(
                        text = message,
                        fontSize = 15.sp,
                        color = colorGrayLight
                    )
                }
                Spacer(modifier = Modifier.height(28.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    content()
                }
            }
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 10.dp, y = (-10).dp)
                    .size(40.dp),
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                IconButton(onClick = { onDismiss() }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = "close",
                        tint = colorGrayBold
                    )
                }
            }
        }
    }
}
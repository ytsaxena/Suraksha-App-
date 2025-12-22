package com.suraksha.app.utility

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suraksha.app.domain.model.PoliceStation
import com.suraksha.app.presentation.theme.colorGrayBold
import com.suraksha.app.presentation.theme.colorGrayLight
import com.suraksha.app.ui.theme.CircleIconButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    modifier: Modifier = Modifier,
    title: String,
    onDismissRequest: () -> Unit = {},
    content: @Composable () -> Unit = {}
) {
    ModalBottomSheet(
        modifier = modifier.systemBarsPadding(),
        onDismissRequest = { onDismissRequest() },
        containerColor = Color.White,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 4.dp)
                    .height(6.dp)
                    .width(60.dp)
                    .clip(RoundedCornerShape(50))
                    .background(colorGrayLight)
            )
        },
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
            confirmValueChange = { true }
        )
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = colorGrayBold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoliceStationBottomSheet(
    modifier: Modifier = Modifier,
    policeStationList: List<PoliceStation>,
    onDismissRequest: () -> Unit = {},
) {
    val context = LocalContext.current
    ModalBottomSheet(
        modifier = modifier.systemBarsPadding(),
        onDismissRequest = { onDismissRequest() },
        containerColor = Color.White,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 4.dp)
                    .height(6.dp)
                    .width(60.dp)
                    .clip(RoundedCornerShape(50))
                    .background(colorGrayLight)
            )
        },
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
            confirmValueChange = { true }
        )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(policeStationList.size) { idx ->
                if (policeStationList[idx].name != null && policeStationList[idx].distance != null) {
                    PoliceStationCard(
                        title = policeStationList[idx].name!!,
                        distance = policeStationList[idx].distance.toString(),
                        onClick = {
                            if (policeStationList[idx].latitude != null && policeStationList[idx].longitude != null) {

                                val lat = policeStationList[idx].latitude!!
                                val lng = policeStationList[idx].longitude!!

                                val uri = Uri.parse(
                                    "https://www.google.com/maps/dir/?api=1&destination=$lat,$lng"
                                )

                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                intent.setPackage("com.google.android.apps.maps")

                                try {
                                    context.startActivity(intent)
                                } catch (e: ActivityNotFoundException) {
                                    // Browser fallback
                                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PoliceStationCard(title: String, distance: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x26525050)),
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .padding(horizontal = 8.dp)
                .fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleIconButton(
                drawableRes = com.suraksha.app.R.drawable.ic_police_station,
                contentDescription = "Police Station",
                backgroundColor = Color.White,
                onClick = null,
                enabled = false,
            )
            Spacer(modifier = Modifier.width(18.dp))
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = colorGrayBold,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "$distance away",
                    fontSize = 14.sp,
                    color = colorGrayLight,
                )
            }
            Image(
                painter = painterResource(id = com.suraksha.app.R.drawable.ic_purple_arrow),
                contentDescription = "Arrow",
                modifier = Modifier
                    .size(20.dp)
                    .clickable {
                        onClick()
                    }
            )
        }
    }
}


@Preview
@Composable
fun PreviewPoliceStationBottomSheet() {
    PoliceStationBottomSheet(
        policeStationList = listOf(
            PoliceStation(
                name = "Title 1",
                distance = 3.0
            ), PoliceStation(
                name = "Title 1",
                distance = 3.0
            ), PoliceStation(
                name = "Title 1",
                distance = 3.0
            ), PoliceStation(
                name = "Title 1",
                distance = 3.0
            )
        ),
        onDismissRequest = {}
    )
}
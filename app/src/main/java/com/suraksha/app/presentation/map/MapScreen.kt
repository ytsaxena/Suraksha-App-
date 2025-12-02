package com.suraksha.app.presentation.map

import android.Manifest
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.suraksha.app.R
import kotlinx.coroutines.launch
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.CameraState
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.material3.CompassButton
import org.maplibre.compose.material3.PointerPinButton
import org.maplibre.compose.material3.ScaleBar
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.style.rememberStyleState
import org.maplibre.spatialk.geojson.Position
import kotlin.time.Duration.Companion.seconds
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.suraksha.app.presentation.theme.White
import com.suraksha.app.presentation.theme.buttonColorEnd
import com.suraksha.app.presentation.theme.buttonColorStart
import com.suraksha.app.presentation.theme.colorGrayBold
import com.suraksha.app.presentation.theme.colorGrayLight
import com.suraksha.app.presentation.theme.negativeColor
import com.suraksha.app.presentation.theme.positiveColor
import kotlin.io.path.Path

private const val DEFAULT_ZOOM = 15.0
private const val ANIMATION_DURATION_SECONDS = 3
private const val PERMISSION_OVERLAY_ALPHA = 0.7f

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(viewModel: MapScreenVM = hiltViewModel()) {

    val location by viewModel.location.collectAsStateWithLifecycle()
    val cameraState = rememberCameraState(
        firstPosition = CameraPosition(target = Position(28.61, 77.20))
    )
    val styleState = rememberStyleState()
    val address by viewModel.address.collectAsStateWithLifecycle()

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(Unit) {
        permissionsState.launchMultiplePermissionRequest()
    }

    LaunchedEffect(permissionsState.allPermissionsGranted) {
        if (permissionsState.allPermissionsGranted) {
            viewModel.fetchCurrentLocation()
        }
    }

    val position = remember(location) {
        location?.let { loc ->
            Position(
                latitude = loc.latitude,
                longitude = loc.longitude
            )
        }
    }
    LaunchedEffect(position) {
        position?.let { pos ->
            viewModel.fetchAddress(pos.latitude, pos.longitude)
            animateCameraToLocation(cameraState, pos)
        }
    }

    var showRateDialog by remember { mutableStateOf(false) }
    var showPoliceBottomSheet by remember { mutableStateOf(false) }

    if (showRateDialog){
        Dialog(
            onDismissRequest = { showRateDialog = false },
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
                        text = stringResource(R.string.rate_this_area),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = colorGrayBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stringResource(R.string.is_this_area_safe_or_unsafe),
                        fontSize = 15.sp,
                        color = colorGrayLight
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = {  },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(containerColor = positiveColor)
                            ) {
                                Text(stringResource(R.string.safe), color = White)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Button(
                                onClick = {  },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(containerColor = negativeColor)
                            ) {
                                Text(
                                    text = stringResource(R.string.unsafe),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        TextButton(
                            onClick = { showRateDialog = false },
                            modifier = Modifier.padding(top = 16.dp).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = colorGrayBold
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.unsafe),
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        MaplibreMap(
            cameraState = cameraState,
            styleState = styleState,
            options = MapOptions(ornamentOptions = OrnamentOptions.OnlyLogo),
            baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty")
        )
        position?.let { pos ->
            MapOverlays(cameraState = cameraState, position = pos, address = address)
        }
        if (!permissionsState.allPermissionsGranted) {
            Text(
                text = "Location permission required",
                modifier = Modifier
                    .background(Color.Red.copy(alpha = PERMISSION_OVERLAY_ALPHA))
                    .padding(12.dp),
                color = Color.White
            )
        }
        BottomButtons(
            onRateClicked = {showRateDialog = true},
            onPoliceClicked = {showPoliceBottomSheet = true}
        )
    }
}

@Composable
fun BoxScope.BottomButtons(
    onRateClicked: () -> Unit = {},
    onPoliceClicked: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .align (Alignment.BottomCenter)
            .padding(16.dp)
            .padding(12.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = {  },
            elevation = null,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = Color.Transparent
            ),
            contentPadding = PaddingValues(0.dp),
            shape = RoundedCornerShape(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(buttonColorStart, buttonColorEnd)
                        )
                    )
                    .clickable(onClick = { onRateClicked() }),
                contentAlignment = Alignment.Center
            ) {
                Row {
                    Icon(
                        painter = painterResource(R.drawable.ic_rate),
                        contentDescription = "rate",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Rate this area",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            OutlinedButton(
                onClick = { onPoliceClicked() },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    1.dp, colorGrayBold
                ),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
            ) {
                Row {
                    Icon(
                        painter = painterResource(R.drawable.ic_police),
                        contentDescription = "police",
                        tint = colorGrayBold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Police Station", color = colorGrayBold)
                }
            }
        }
    }
}

@Composable
private fun MapOverlays(
    cameraState: CameraState,
    position: Position,
    address: String?
) {
    val coroutineScope = rememberCoroutineScope()
    val marker = painterResource(R.drawable.ic_location_marker)

    val screenLocation = remember(cameraState.position.target, cameraState.position.zoom) {
        cameraState.projection?.screenLocationFromPosition(position)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ScaleBar(
            metersPerDp = cameraState.metersPerDpAtTarget,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
        )

        Column(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.BottomEnd)
        ) {
            IconButton(
                onClick = {
                    coroutineScope.launch { animateCameraToLocation(cameraState, position) }
                }
            ) {
                Image(
                    painter = marker,
                    contentDescription = "Recenter to current location"
                )
            }
            CompassButton(cameraState = cameraState)
        }

        PointerPinButton(
            cameraState = cameraState,
            targetPosition = position,
            onClick = { coroutineScope.launch { animateCameraToLocation(cameraState, position) } }
        ) {
            Image(
                painter = marker,
                contentDescription = "Current location marker"
            )
        }

        screenLocation?.let { screenPos ->

            val pinWidth = 32.dp
            val pinHeight = 32.dp

            val bubbleWidth = 180.dp
            val bubbleHeight = 48.dp
            val triangleSize = 12.dp

            Box(
                modifier = Modifier
                    .offset(
                        x = screenPos.x - (bubbleWidth / 2),
                        y = screenPos.y - pinHeight - pinHeight - bubbleHeight
                    )
                    .background(Color.White, RoundedCornerShape(10.dp))
                    .padding(8.dp)
                    .size(width = bubbleWidth, height = bubbleHeight)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = address ?: "Fetching...",
                    color = Color.Black,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Canvas(
                modifier = Modifier
                    .offset(
                        x = screenPos.x - (triangleSize / 2),
                        y = screenPos.y - pinHeight - triangleSize - (triangleSize / 2)
                    )
                    .size(triangleSize)
            ) {
                val path = Path().apply {
                    moveTo(size.width / 2, size.height)
                    lineTo(0f, 0f)
                    lineTo(size.width, 0f)
                }
                drawPath(path, color = Color.White)
            }

            Image(
                painter = marker,
                contentDescription = "location pin",
                modifier = Modifier
                    .offset(
                        x = screenPos.x - (pinWidth / 2),
                        y = screenPos.y - pinHeight
                    )
                    .size(pinWidth, pinHeight)
            )
        }
    }
}


private suspend fun animateCameraToLocation(
    cameraState: CameraState,
    position: Position
) {
    cameraState.animateTo(
        finalPosition = cameraState.position.copy(
            target = position,
            zoom = DEFAULT_ZOOM
        ),
        duration = ANIMATION_DURATION_SECONDS.seconds
    )
}

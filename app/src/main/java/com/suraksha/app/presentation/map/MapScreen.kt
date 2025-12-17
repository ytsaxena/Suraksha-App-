package com.suraksha.app.presentation.map

import android.Manifest
import android.os.Handler
import android.os.Looper.getMainLooper
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.suraksha.app.R
import com.suraksha.app.domain.model.PoliceStation
import com.suraksha.app.domain.model.Rating
import com.suraksha.app.presentation.theme.Purple40
import com.suraksha.app.presentation.theme.White
import com.suraksha.app.presentation.theme.buttonColorEnd
import com.suraksha.app.presentation.theme.buttonColorStart
import com.suraksha.app.presentation.theme.colorGrayBold
import com.suraksha.app.presentation.theme.colorGrayLight
import com.suraksha.app.presentation.theme.negativeColor
import com.suraksha.app.presentation.theme.positiveColor
import com.suraksha.app.utility.Constants
import com.suraksha.app.utility.DialogBox
import com.suraksha.app.utility.PoliceStationBottomSheet
import kotlinx.coroutines.delay
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
    val safePercent by viewModel.safePercent.collectAsState()
    val unsafePercent by viewModel.unsafePercent.collectAsState()

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
    var showUnsafeAreaDialog by remember { mutableStateOf(false) }

    val ratingSaved by viewModel.ratingSaved.collectAsState()

    LaunchedEffect(ratingSaved) {
        if (ratingSaved) {
            delay(2000)
            viewModel.resetRatingSaved()
        }
    }

    if (showRateDialog){
        DialogBox(
            onDismiss = { showRateDialog = false },
            title = stringResource(R.string.rate_this_area),
            message = stringResource(R.string.is_this_area_safe_or_unsafe),
            content = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            viewModel.saveLocationRating(Rating.SAFE, address ?: "")
                            showRateDialog = false
                                  },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = positiveColor)
                    ) {
                        Row (
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Icon(
                                painter = painterResource(R.drawable.ic_rate),
                                contentDescription = "rate",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(stringResource(R.string.safe), color = White)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            showUnsafeAreaDialog = true
                            showRateDialog = false
                                  },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = negativeColor)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_dislike),
                                contentDescription = "rate",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = stringResource(R.string.unsafe),
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        )
    }

    if (showUnsafeAreaDialog) {
        DialogBox(
            onDismiss = { showUnsafeAreaDialog = false },
            title = stringResource(R.string.tell_us_what_happened)
        ) {

            val options = listOf(
                stringResource(R.string.was_it_too_dark),
                stringResource(R.string.was_anyone_following_you),
                stringResource(R.string.were_there_no_police_nearby),
                stringResource(R.string.did_you_face_harassment),
                stringResource(R.string.poor_lighting_conditions)
            )

            val selected = remember { mutableStateListOf(false, false, false, false, false) }

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                options.forEachIndexed { index, text ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = selected[index],
                            onCheckedChange = { selected[index] = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Purple40,
                                uncheckedColor = colorGrayLight
                            )
                        )
                        Text(
                            text = text,
                            color = colorGrayLight,
                            fontSize = 15.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = { },
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
                            .clickable(onClick = {
                                viewModel.saveLocationRating(Rating.UNSAFE, address ?: "")
                                showUnsafeAreaDialog = false
                            }),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.submit_report),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    OutlinedButton(
                        onClick = { showUnsafeAreaDialog = false},
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = colorGrayBold
                        ),
                        border = BorderStroke(1.dp, colorGrayBold),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        var showProgress by remember { mutableStateOf(false) }
        var errorStatus by remember { mutableStateOf(false) }
        var policeStations by remember { mutableStateOf<List<PoliceStation>>(emptyList()) }
        var loadingKey by remember { mutableStateOf(0) }

        // This will trigger every time loadingKey changes
        LaunchedEffect(loadingKey) {
            if (loadingKey > 0) {
                val lat = location?.latitude
                val lon = location?.longitude

                if (lat != null && lon != null) {
                    showProgress = true
                    policeStations = emptyList()

                    viewModel.findNearbyPoliceStations(
                        latitude = lat,
                        longitude = lon,
                        radiusKm = 50,
                        onSuccess = {
                            showProgress = false
                            policeStations = it
                        },
                        onError = {
                            showProgress = false
                            errorStatus = true
                        }
                    )
                } else {
                    showProgress = false
                }
            }
        }

        if (showPoliceBottomSheet && policeStations.isNotEmpty() && !showProgress) {
            PoliceStationBottomSheet(
                policeStationList = policeStations,
                onDismissRequest = {
                    showPoliceBottomSheet = false
                    policeStations = emptyList()
                    showProgress = false
                }
            )
        }


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
            onRateClicked = { showRateDialog = true },
            onPoliceClicked = {
                viewModel.fetchCurrentLocation()
                showPoliceBottomSheet = true
                loadingKey++
            }
        )
        SafetyRatingCard(
            safePercent = safePercent,
            unsafePercent = unsafePercent,
            onReloadClicked = { viewModel.reloadSafetyRating() },
            modifier = Modifier
        )
        if (ratingSaved) {
            RatingSavedToast(
                message = "Thank you! Your response has been saved.",
                status = Constants.STATUS_POSITIVE
            )
        }

        if (showProgress) {
            println("Rendering progress bar")
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressBar(showProgress = remember { mutableStateOf(true) })
            }
        }

        if (errorStatus) {
            Handler(getMainLooper()).postDelayed({ errorStatus = false }, 1000)
            RatingSavedToast(
                message = "Something went wrong",
                status = Constants.STATUS_NEGATIVE
            )
        }

    }
}


@Composable
fun CircularProgressBar(
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 16.dp,
    backgroundColor: Color = Color(0xFF6B6B6B),
    progressColor: Color = Color.White,
    size: Dp = 100.dp,
    durationMillis: Int = 1000,
    showProgress: MutableState<Boolean>,
) {
    var progress by remember { mutableStateOf(0f) }

    LaunchedEffect(showProgress) {
        while (showProgress.value) {
            animate(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = durationMillis,
                    easing = LinearEasing
                )
            ) { value, _ ->
                progress = value
            }
        }
    }

    if (showProgress.value) {
        Canvas(
            modifier = modifier.size(size)
        ) {
            val canvasSize = size.toPx()
            val radius = (canvasSize / 2) - (strokeWidth.toPx() / 2)
            val center = Offset(canvasSize / 2, canvasSize / 2)

            drawArc(
                color = backgroundColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(
                    center.x - radius,
                    center.y - radius
                ),
                size = Size(radius * 2, radius * 2),
                style = Stroke(
                    width = strokeWidth.toPx(),
                    cap = StrokeCap.Round
                )
            )

            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = Offset(
                    center.x - radius,
                    center.y - radius
                ),
                size = Size(radius * 2, radius * 2),
                style = Stroke(
                    width = strokeWidth.toPx(),
                    cap = StrokeCap.Round
                )
            )
        }
    }
}

@Composable
fun BoxScope.BottomButtons(
    onRateClicked: () -> Unit = {},
    onPoliceClicked: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .align(Alignment.BottomCenter)
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

@Composable
fun RatingSavedToast(
    message: String,
    status: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 84.dp, bottom = 32.dp, end = 32.dp, start = 32.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (status == Constants.STATUS_POSITIVE) positiveColor else negativeColor)
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (status == Constants.STATUS_POSITIVE) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(13.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                color = Color.White,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun SafetyRatingCard(
    safePercent: Int?,
    unsafePercent: Int?,
    onReloadClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(12.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {

            Column {
                Text(
                    text = "Safety Rating in this area",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorGrayBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    if (safePercent != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_safe_rating),
                                contentDescription = "Safe",
                                tint = positiveColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$safePercent% safe",
                                fontSize = 12.sp,
                                color = colorGrayLight
                            )
                        }
                    }

                    if (unsafePercent != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_unsafe_rating),
                                contentDescription = "Unsafe",
                                tint = negativeColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$unsafePercent% unsafe",
                                fontSize = 12.sp,
                                color = colorGrayLight
                            )
                        }
                    }
                }
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_reload),
                contentDescription = "Reload",
                modifier = Modifier
                    .size(22.dp)
                    .clickable { onReloadClicked() }
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

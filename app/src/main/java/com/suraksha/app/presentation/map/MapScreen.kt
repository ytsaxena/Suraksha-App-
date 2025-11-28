package com.suraksha.app.presentation.map

import android.Manifest
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
            animateCameraToLocation(cameraState, pos)
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
            MapOverlays(cameraState = cameraState, position = pos)
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
    }
}

@Composable
private fun MapOverlays(
    cameraState: CameraState,
    position: Position
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

        screenLocation?.let { target ->
            Image(
                modifier = Modifier.offset(x = target.x, y = target.y),
                painter = marker,
                contentDescription = "Location indicator"
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

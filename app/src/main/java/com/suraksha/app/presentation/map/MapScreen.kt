package com.suraksha.app.presentation.map

import android.Manifest
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.suraksha.app.R
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    viewModel: MapScreenVM = hiltViewModel()
) {
    val location by viewModel.location.collectAsState()
    val context = LocalContext.current

    val permissions = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(Unit) {
        permissions.launchMultiplePermissionRequest()
    }

    LaunchedEffect(permissions.allPermissionsGranted) {
        if (permissions.allPermissionsGranted) {
            viewModel.fetchCurrentLocation()
        }
    }

    val mapView = remember {
        MapView(context)
    }

    var mapLibreMap by remember { mutableStateOf<MapLibreMap?>(null) }

//    DisposableEffect(mapView) {
//        mapView.onStart()
//        mapView.onResume()
//        onDispose {
//            mapView.onPause()
//            mapView.onStop()
//            mapView.onDestroy()
//        }
//    }

    var cameraReady by remember { mutableStateOf(false) }

    LaunchedEffect(location) {
        location?.let { loc ->
            mapView.getMapAsync { map ->
                mapLibreMap = map

                map.setStyle("https://demotiles.maplibre.org/style.json") {

                    val target = LatLng(loc.latitude, loc.longitude)
                    val update = CameraUpdateFactory.newLatLngZoom(target, 15.0)

                    cameraReady = false

                    map.animateCamera(update, object : MapLibreMap.CancelableCallback {
                        override fun onFinish() {
                            cameraReady = true
                        }

                        override fun onCancel() {}
                    })
                }
            }
        }
    }

    val screenPoint = remember(location, mapLibreMap) {
        location?.let { loc ->
            mapLibreMap?.projection?.toScreenLocation(
                LatLng(loc.latitude, loc.longitude)
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { mapView }
        )

        screenPoint?.let { pt ->
            Column(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            pt.x.toInt() - 24,
                            pt.y.toInt() - 48
                        )
                    }
                    .wrapContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "You are here",
                        color = Color.Black
                    )
                }

                Spacer(Modifier.height(6.dp))
                Image(
                    painter = painterResource(R.drawable.ic_location_marker),
                    contentDescription = "Your Location",
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        if (!permissions.allPermissionsGranted) {
            Text(
                text = "Location permission required",
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(Color.Red.copy(alpha = 0.7f))
                    .padding(12.dp),
                color = Color.White
            )
        }
    }
}
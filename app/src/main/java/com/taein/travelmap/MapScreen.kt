/*
 * Copyright 2022 SOUP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.taein.travelmap

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberMarkerState
import com.naver.maps.map.overlay.OverlayImage

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun MapScreen() {
    val viewModel: MapViewModel = viewModel()
    val context: Context = LocalContext.current
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        imageUris = uris
        viewModel.processImageUri(context, imageUris)
    }

    Scaffold(
        topBar = {},
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.onPrimary,
                onClick = {
                    launcher.launch("image/*")
                }
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = "camera icon"
                )
            }
        }
    ) { contentPadding ->
        NaverMap(
            contentPadding = contentPadding
        ) {
            RequireImages()
            val uiState by viewModel.uiState.collectAsState()
            if (uiState.id != "-1") {
                DisplayImageWithCoil(
                    uri = uiState.uri,
                    lan = uiState.gpsLatitude,
                    lon = uiState.gpsLongitude)
                Log.d(AppArgs.TAG, "uiState.latitude : ${uiState.gpsLatitude}")
            }
        }
    }
}

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun DisplayImageWithCoil(
    uri: Uri,
    lan: Double,
    lon: Double,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    /*AndroidView(
        factory = { ctx ->
            ImageView(ctx).apply {
                // ImageView 설정
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        },
        modifier = Modifier.fillMaxWidth(),
        update = { imageView ->
            imageView.load(uri, context.imageLoader) {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_background)
                error(R.drawable.ic_launcher_foreground)
                scale(Scale.FILL)
            }
            Marker(
                state = rememberMarkerState(
                    position = LatLng(
                        uiState.gpsLatitude,
                        uiState.gpsLongitude
                    )
                ),
                //icon = OverlayImage.fromResource(R.drawable.ic_launcher_background),
                icon = OverlayImage.fromView(DisplayImageWithCoil(uiState.uri, Modifier.fillMaxSize())),
                width = dimensionResource(R.dimen.marker_size),
                height = dimensionResource(R.dimen.marker_size),
                isFlat = true,
                angle = 90f,
            )
        }
    )*/

    Marker(
        state = rememberMarkerState(
            position = LatLng(
                lan,
                lon
            )
        ),
        //icon = OverlayImage.fromResource(R.drawable.ic_launcher_background),
        icon = OverlayImage.fromView(createCustomView(context)),
        width = dimensionResource(R.dimen.marker_size),
        height = dimensionResource(R.dimen.marker_size),
        isFlat = true,
        angle = 90f,
    )
}

fun createCustomView(context: Context): View {
    val textView = TextView(context)
    textView.text = "Hello from TextView"
    return textView
}

@Composable
internal fun RequireImages() {
    var isGranted by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permission ->
        when {
            permission -> isGranted = true
            !permission -> isGranted = false
        }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        RequestPermission(context, requestPermissionLauncher)
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
internal fun RequestPermission(
    context: Context,
    requestPermissionLauncher: ActivityResultLauncher<String>
) {
    when {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_MEDIA_LOCATION
        ) != PackageManager.PERMISSION_GRANTED -> {
            Log.d(AppArgs.TAG, "PERMISSION_TEST > requires permission")
            SideEffect {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_MEDIA_LOCATION)
            }
        }

        else -> {
            Log.d(AppArgs.TAG, "PERMISSION_TEST > else")
            SideEffect {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_MEDIA_LOCATION)
            }
        }
    }
}

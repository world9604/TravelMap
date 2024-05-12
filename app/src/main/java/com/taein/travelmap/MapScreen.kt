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
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberMarkerState
import com.naver.maps.map.overlay.OverlayImage
import java.io.File


@Composable
fun MapScreen(viewModel: MapViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(
        topBar = {},
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(15.dp),
                horizontalAlignment = Alignment.End
            ) {
                AddPhotoActionButton(viewModel)
                CameraActionButton(viewModel)
            }
        }
    ) { contentPadding ->
        OnMapScreen(contentPadding, uiState)
        OutsideMapScreen(uiState)
    }
}

@Composable
private fun OutsideMapScreen(uiState: MapUiState) {
    when (uiState) {
        MapUiState.PhotoNotReady -> Unit
        MapUiState.Loading -> LoadingScreen()
        is MapUiState.Error -> ShowDialog(true, uiState.message)
        else -> Unit
    }
}

@Composable
@OptIn(ExperimentalNaverMapApi::class)
private fun OnMapScreen(
    contentPadding: PaddingValues,
    uiState: MapUiState = MapUiState.Loading
) {
    NaverMap(contentPadding = contentPadding) {
        when (uiState) {
            is MapUiState.Success -> {
                if (uiState.isEmpty()) {
                    Unit
                } else {
                    for (userPhoto in uiState.userPhoto) {
                        DisplayPhoto(
                            uri = userPhoto.uri,
                            lan = userPhoto.gpsLatitude,
                            lon = userPhoto.gpsLongitude
                        )
                    }
                }
            }
            else -> Unit
        }
    }
}

@Composable
private fun ShowDialog(showErrorDialog: Boolean, errorMessage: String) {
    var showErrorDialog1 by remember { mutableStateOf(showErrorDialog) }
    if (showErrorDialog1) {
        AlertDialog(
            onDismissRequest = {
                showErrorDialog1 = false
            },
            title = {
                Text(text = "오류 발생")
            },
            text = {
                Text(text = errorMessage)
            },
            confirmButton = {
                Button(
                    onClick = {
                        showErrorDialog1 = false
                    }
                ) {
                    Text("확인")
                }
            }
        )
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(50.dp),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun CameraActionButton(
    viewModel: MapViewModel
) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val file = File(context.externalMediaDirs.first(), "${System.currentTimeMillis()}.jpg")
    if (!file.exists()) file.createNewFile()
    imageUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    Log.d(AppArgs.TAG, "CameraActionButton2 imageUri 1 : $imageUri")

    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        imageUri?.let { uri ->
            if (success) {
                Log.d(AppArgs.TAG, "CameraActionButton2 imageUri 2 : $uri")
                viewModel.processImageUri(context, uri)
            } else {
                Log.d(AppArgs.TAG, "CameraActionButton2 fail")
            }
        } ?: run {
            Log.d(AppArgs.TAG, "CameraActionButton2 imageUri is null")
        }
    }

    FloatingActionButton(
        containerColor = MaterialTheme.colorScheme.onPrimary,
        onClick = {
            takePictureLauncher.launch(imageUri)
        }
    ) {
        Icon(
            imageVector = Icons.Default.PhotoCamera,
            contentDescription = "camera icon"
        )
    }
}

@Composable
private fun AddPhotoActionButton(
    viewModel: MapViewModel
) {
    val context: Context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        viewModel.processImageUri(context, uris)
    }

    FloatingActionButton(
        containerColor = MaterialTheme.colorScheme.onPrimary,
        onClick = {
            launcher.launch("image/*")
        }
    ) {
        Row(modifier = Modifier.padding(horizontal = 16.dp)) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = "Add Photo",
                modifier = Modifier.padding(end = 5.dp)
            )
            Text(
                text = "Add Photo"
            )
        }
    }
}

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun DisplayPhoto(
    uri: Uri,
    lan: Double,
    lon: Double,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Marker(
        state = rememberMarkerState(
            position = LatLng(
                lan,
                lon
            )
        ),
        icon = OverlayImage.fromView(createCustomView(context, uri)),
        /*width = dimensionResource(R.dimen.photo_marker_size),
        height = dimensionResource(R.dimen.photo_marker_size),
        isFlat = true,*/
        angle = 90f,
    )
}

fun createCustomView(
    context: Context,
    uri: Uri
): View {
    val inflater = LayoutInflater.from(context)
    val rootView = inflater.inflate(R.layout.user_photo_view, null) as ViewGroup
    val imageView = rootView.findViewById<ImageView>(R.id.user_photo)
    imageView.setImageURI(uri)
    return rootView
}

fun loadImageWithRoundedCorners(context: Context, imageView: ImageView, uri: Uri) {
    val options = RequestOptions().transform(RoundedCorners(20))
    Glide.with(context)
        .load(uri)
        .apply(options)
        .into(imageView)
}

fun roundCorners(context: Context, uri: Uri): Bitmap? {
    val inputStream = context.contentResolver.openInputStream(uri)
    val original = BitmapFactory.decodeStream(inputStream)
    val width = original.width
    val height = original.height
    val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)

    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val rect = Rect(0, 0, width, height)
    val rectF = RectF(rect)
    val radius = 100.0f
    paint.isAntiAlias = true
    canvas.drawRoundRect(rectF, radius, radius, paint)

    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(original, rect, rect, paint)

    original.recycle()
    return output
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

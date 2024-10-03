package com.taein.travelmap.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberMarkerState
import com.naver.maps.map.overlay.OverlayImage
import com.taein.travelmap.AppArgs
import com.taein.travelmap.R
import java.io.File


@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel(),
    onNavigateToDetailPhotoMarker : (String) -> Unit = {},
) {
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
        OnMapScreen(contentPadding, uiState, onNavigateToDetailPhotoMarker)
        OutsideMapScreen(uiState)
    }
}

@Composable
private fun OutsideMapScreen(uiState: MapUiState) {
    when (uiState) {
        is MapUiState.PhotoNotReady -> Unit
        is MapUiState.Loading -> LoadingScreen()
        is MapUiState.Error -> ShowDialog(uiState.message)
        is MapUiState.PhotoNotLoad -> ShowDialog(stringResource(R.string.no_image_with_location_info))
        else -> Unit
    }
}

@Composable
@OptIn(ExperimentalNaverMapApi::class)
private fun OnMapScreen(
    contentPadding: PaddingValues,
    uiState: MapUiState = MapUiState.Loading,
    onPhotoClick: (String) -> Unit
) {
    NaverMap(contentPadding = contentPadding) {
        when (uiState) {
            is MapUiState.Success -> {
                if (uiState.isEmpty()) {
                    Unit
                } else {
                    for (userPhoto in uiState.photoMarker) {
                        DisplayPhoto(
                            id = userPhoto.id,
                            uri = userPhoto.uri,
                            lan = userPhoto.gpsLatitude,
                            lon = userPhoto.gpsLongitude,
                            onPhotoClick = { onPhotoClick(userPhoto.id) }
                        )
                    }
                }
            }
            else -> Unit
        }
    }
}

@Composable
private fun ShowDialog(message: String) {
    var showDialog by remember { mutableStateOf(true) }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
            },
            title = {
                Text(text = "오류 발생")
            },
            text = {
                Text(text = message)
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
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
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(uri.path),
                    null
                ) { path, uriType ->
                    Log.d(AppArgs.TAG, "MediaScanner scanned: $path -> $uriType")
                    viewModel.processImageUri(context, uri)
                }
            } else {
                Log.d(AppArgs.TAG, "CameraActionButton2 fail")
            }
        } ?: run {
            Log.d(AppArgs.TAG, "CameraActionButton2 imageUri is null")
        }
    }

    RequireCameraAndLocationPermission(
        requestPermission = {
            RequestCameraPermissionDialog {
                takePictureLauncher.launch(imageUri)
            }
        }
    ) {
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
}

@Composable
fun RequireCameraAndLocationPermission(
    requestPermission: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    var isGranted by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_MEDIA_LOCATION
    )

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        isGranted = permissionsMap.values.all { it }
    }

    LaunchedEffect(key1 = isGranted) {
        val allGranted = permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
        if (!allGranted) {
            requestPermissionLauncher.launch(permissions)
        } else {
            isGranted = true
        }
    }

    if (isGranted) {
        content()
    } else {
        requestPermission()
    }
}

@Composable
private fun RequestCameraPermissionDialog(onRequestPermission: () -> Unit) {
    AlertDialog(
        onDismissRequest = { /* 사용자가 다이얼로그를 닫을 때의 처리 */ },
        title = { Text("권한 요청") },
        text = { Text("이 기능을 사용하려면 카메라 권한이 필요합니다.") },
        confirmButton = {
            Button(
                onClick = onRequestPermission
            ) {
                Text("권한 요청")
            }
        }
    )
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
    id: String,
    uri: Uri,
    lan: Double,
    lon: Double,
    onPhotoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Button(
        onClick = { onPhotoClick() }
    ) {
        Marker(
            state = rememberMarkerState(position = LatLng(lan, lon)),
            icon = OverlayImage.fromView(createCustomView(context, uri)),
            /*width = dimensionResource(R.dimen.photo_marker_size),
            height = dimensionResource(R.dimen.photo_marker_size),
            isFlat = true,*/
            angle = 90f,
        )
    }
}

fun createCustomView(
    context: Context,
    uri: Uri
): View {
    val inflater = LayoutInflater.from(context)
    val rootView = inflater.inflate(R.layout.user_photo_view, null) as ViewGroup
    val imageView = rootView.findViewById<ImageView>(R.id.user_photo)
    imageView.setImageURI(uri)

    val cardView = rootView.findViewById<CardView>(R.id.card_view)
    cardView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    return rootView
}
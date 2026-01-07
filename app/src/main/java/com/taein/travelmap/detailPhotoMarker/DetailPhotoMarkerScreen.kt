package com.taein.travelmap.detailPhotoMarker

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import com.taein.travelmap.ui.theme.TravelMapTheme


@Preview(showBackground = true)
@Composable
fun DetailPhotoMarkerScreenPreview() {
    TravelMapTheme {
        val fakeDiary = Diary(
            id = "12312355",
            date = "2023102312030",
            photo = listOf(
                Uri.parse("https://i.namu.wiki/i/kR2p0TduTIsGOT09zbjEBk2Cd9qMOefNPhAuHgZZUBPgEZt3eWYnah8ju8VYrrfAn-dATZJYvXIvmPG4UaS4aaRO5-ypd8wiaE6t5MZ0Ms7y3jUFpjaAEcNsLx52I60ataqwX61KyhlI4ZSO8YRiVw.webp")
            ),
            contents = "여기 내용을 입력하세요"
        )
        DetailPhotoMarker(diary = fakeDiary, onDiaryContentsChange = {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailPhotoMarkerScreen(
    viewModel: DetailPhotoMarkerViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.detailPhotoMarkerUiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Diary")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.deleteDiary {
                                onNavigateBack()
                            }
                        }
                    ) {
                        Text(
                            text = "Delete",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when (uiState) {
            is DetailPhotoMarkerUiState.Error -> TODO()
            DetailPhotoMarkerUiState.Loading -> LoadingScreen()
            DetailPhotoMarkerUiState.NotShown -> TODO()
            is DetailPhotoMarkerUiState.PhotoUploadSuccess -> {
                val successState = uiState as DetailPhotoMarkerUiState.PhotoUploadSuccess
                DetailPhotoMarker(
                    diary = successState.diary,
                    modifier = Modifier.padding(innerPadding),
                    onDiaryContentsChange = { newText ->
                        viewModel.updateDiaryContents(newText)
                    }
                )
            }
        }
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
        CircularProgressIndicator()
    }
}

@Composable
private fun DetailPhotoMarker(
    diary: Diary,
    modifier: Modifier = Modifier,
    onDiaryContentsChange: (String) -> Unit
) {
    Column {
        Date(diary.date, modifier.padding(top = 20.dp))
        MainPhoto(diary.photo, modifier.padding(top = 10.dp))
        TextContents(diary.contents, modifier.padding(top = 10.dp)) { newContents ->
            onDiaryContentsChange(newContents)
        }
    }
}

@Composable
private fun Date(date: String, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier.padding(start = 20.dp, end = 20.dp),
        text = date,
        style = MaterialTheme.typography.labelSmall
    )
}

@Composable
private fun TextContents(
    initialText: String,
    modifier: Modifier = Modifier,
    onTextChanged: (String) -> Unit
) {
    var observerText by remember { mutableStateOf(initialText) }
    TextField(
        value = observerText,
        onValueChange = {
            observerText = it
            onTextChanged(it)
        },
        placeholder = { PlaceholderContents() },
        modifier = modifier,
        textStyle = MaterialTheme.typography.bodyLarge,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = MaterialTheme.colorScheme.onTertiary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.onTertiary,
            focusedContainerColor = MaterialTheme.colorScheme.onTertiary,
            unfocusedContainerColor = MaterialTheme.colorScheme.onTertiary,
            focusedPlaceholderColor = MaterialTheme.colorScheme.onSecondary,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSecondary,
            focusedTextColor = MaterialTheme.colorScheme.onSecondary,
            unfocusedTextColor = MaterialTheme.colorScheme.onSecondary
        )
    )
}

@Composable
private fun PlaceholderContents() {
    Text(
        text = "그날의 기억을 적어주세요.\n그날의 날씨, 맛, 향, 감정, 사람들...\n어떤것이든 좋아요!\n남는건 추억뿐일거에요..☺",
        color = MaterialTheme.colorScheme.onSecondary
    )
}

@Composable
fun MainPhoto(
    imageUris: List<Uri>,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { imageUris.size })

    HorizontalPager(
        state = pagerState,
        modifier = modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
            .fillMaxHeight(0.55f)
    ) { page ->
        Image(
            painter = rememberImagePainter(data = imageUris[page]),
            contentDescription = "Detail Photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                //.aspectRatio(16f / 9f)
                .shadow(5.dp, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
        )
    }
}

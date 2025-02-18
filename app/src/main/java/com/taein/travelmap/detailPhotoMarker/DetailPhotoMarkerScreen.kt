package com.taein.travelmap.detailPhotoMarker

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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


@Preview(showBackground = true)
@Composable
fun DetailPhotoMarkerScreenPreview() {
    /*TravelMapTheme {
        val fakeDiary = Diary(
            id = "12312355",
            date = "2023102312030",
            photo = listOf("https://example.com/photo.jpg"),
            contents = "여기 내용을 입력하세요"
        )
        DetailPhotoMarker(diary = fakeDiary)
    }*/
}


@Composable
fun DetailPhotoMarkerScreen(
    viewModel: DetailPhotoMarkerViewModel = hiltViewModel()
) {
    val modifier = Modifier
    val uiState by viewModel.detailPhotoMarkerUiState.collectAsState()

    when (uiState) {
        is DetailPhotoMarkerUiState.Error -> TODO()
        DetailPhotoMarkerUiState.Loading -> LoadingScreen()
        DetailPhotoMarkerUiState.NotShown -> TODO()
        is DetailPhotoMarkerUiState.PhotoUploadSuccess -> {
            val successState = uiState as DetailPhotoMarkerUiState.PhotoUploadSuccess
            DetailPhotoMarker(successState.diary, modifier)
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
    modifier: Modifier = Modifier
) {
    Column {
        Date(diary.date, modifier.padding(top = 20.dp))
        MainPhoto(diary.photo, modifier.padding(top = 10.dp))
        TextContents(diary.contents, modifier.padding(top = 10.dp))
    }
}

@Composable
private fun Date(date: String, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier.padding(start = 20.dp, end = 20.dp),
        text = formatDateTime(date),
        style = MaterialTheme.typography.labelSmall
    )
}

fun formatDateTime(input: String): String {
    if (input.length < 12) {
        throw IllegalArgumentException("입력 문자열은 최소 12자리 이상이어야 합니다.")
    }
    val normalized = input.substring(0, 12)

    val year = normalized.substring(0, 4)
    val month = normalized.substring(4, 6)
    val day = normalized.substring(6, 8)
    val hour = normalized.substring(8, 10)
    val minute = normalized.substring(10, 12)

    return "${year}년 ${month}월 ${day}일 $hour:$minute"
}

@Composable
private fun TextContents(text: String, modifier: Modifier = Modifier) {
    var text1 = text
    TextField(
        value = "",
        onValueChange = { text1 = it },
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
            .height(500.dp)
    ) { page ->
        Image(
            painter = rememberImagePainter(data = imageUris[page]),
            contentDescription = "Detail Photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.dp, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
        )
    }
}

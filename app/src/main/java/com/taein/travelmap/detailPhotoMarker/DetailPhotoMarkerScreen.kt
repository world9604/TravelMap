package com.taein.travelmap.detailPhotoMarker

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter

@Preview(showBackground = true)
@Composable
fun DetailPhotoMarkerScreenPreview() {
    DetailPhotoMarkerScreen()
}

@Composable
fun DetailPhotoMarkerScreen(
    viewModel: DetailPhotoMarkerViewModel = hiltViewModel()
) {
    val photo = "https://images.unsplash.com/photo-1717155736971-b53c6dfd940f?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D" // 실제 URL로 대체하세요
    val date = "2023년 10월 23일 오후 8:20"
    val diary by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()
    val modifier = Modifier

    when (uiState) {
        is DetailPhotoMarkerUiState.Error -> TODO()
        DetailPhotoMarkerUiState.Loading -> TODO()
        DetailPhotoMarkerUiState.NotShown -> TODO()
        is DetailPhotoMarkerUiState.PhotoUploadSuccess -> DetailPhotoMarker(uiState.diary, modifier)
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
        TextContents(diary.contents, modifier.padding(top = 20.dp))
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
private fun TextContents(text: String, modifier: Modifier = Modifier) {
    var text1 = text
    TextField(
        value = text1,
        onValueChange = { text1 = it },
        placeholder = { PlaceholderContents() },
        modifier = modifier.padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
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
        text = "그날의 기억을 적어주세요.\n그날의 날씨, 맛, 향, 감정정, 사람들...\n어떤것이든 좋아요!\n남는건 추억뿐일거에요..☺",
        color = MaterialTheme.colorScheme.onSecondary
    )
}

@Composable
fun MainPhoto(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    Image(
        painter = rememberImagePainter(data = imageUrl),
        contentDescription = "Detail Photo",
        modifier = modifier
            .padding(start = 10.dp, end = 10.dp)
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
    )
}
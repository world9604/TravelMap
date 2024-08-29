package com.taein.travelmap.detailHotPlace

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter

@Preview(showBackground = true)
@Composable
fun DetailHotPlaceScreenPreview() {
    DetailHotPlaceScreen()
}

@Composable
fun DetailHotPlaceScreen() {
    val mainImage = "https://images.unsplash.com/photo-1717155736971-b53c6dfd940f?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D" // 실제 URL로 대체하세요
    val timestamp = "2023년 10월 23일 오후 8:20"
    val description = "이날은 삼총사가 각각 여자친구를 데리고와서 모임을 하기로 한 날이었어..."

    val images = listOf(
        "https://images.unsplash.com/photo-1717620378082-b2b48c2a8b93?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
        "https://images.unsplash.com/photo-1717297808345-b740e9846158?q=80&w=2056&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
        "https://plus.unsplash.com/premium_photo-1708589337826-af68526340e7?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
        "https://images.unsplash.com/photo-1717501218565-30faf6f3dc66?q=80&w=1932&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
    )

    Column {
        MainPhotoSlider(mainImage, timestamp, description)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = description, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(16.dp))
        Spacer(modifier = Modifier.height(16.dp))
        SubPhotoSlider(images)
    }
}

@Composable
fun MainPhotoSlider(imageUrl: String, timestamp: String, description: String) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Image(
            painter = rememberImagePainter(data = imageUrl),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(8.dp))
                .align(Alignment.Center)
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .background(Color(0x80000000))
                .padding(8.dp)
        ) {
            Text(text = timestamp, color = Color.White, style = MaterialTheme.typography.bodyMedium)
            Text(text = description, color = Color.White, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun SubPhotoSlider(images: List<String>) {
    LazyRow {
        items(images.size) { index ->
            val imageUrl = images[index]
            Image(
                painter = rememberImagePainter(data = imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }
    }
}
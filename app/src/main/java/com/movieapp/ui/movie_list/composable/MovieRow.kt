package com.movieapp.ui.movie_list.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.movieapp.domain.model.custom.CustomMovieModel
import com.movieapp.ui.theme.netflix_gray_2

@Composable
fun MovieRow(text: String, list: List<CustomMovieModel>, onItemClicked: (String) -> Unit,onMoreClicked:()->Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(start = 5.dp),
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color.White
            )
        )
        Text(
            "Xem thêm",
            modifier = Modifier.padding(end = 5.dp).clickable{
                onMoreClicked()
            },
            style = MaterialTheme.typography.titleSmall.copy(
                color = netflix_gray_2
            )
        )
    }
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        if (list.isEmpty()) {
            items(count = 10) {
                MovieItem(movie = null) {
                    onItemClicked(it)
                }
            }
        } else {
            items(count = if(list.size > 10) 10 else list.size ) { index ->
                MovieItem(movie = list[index]) { string ->
                    onItemClicked(string)
                }
            }
        }

    }
}
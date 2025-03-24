package com.movieapp.ui.movie_list.composable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.movieapp.domain.model.custom.CustomMovieModel

@Composable
fun MovieRow(text: String, list: List<CustomMovieModel>, onClicked: (String) -> Unit) {
    Text(
        text = text,
        modifier = Modifier.padding(start = 5.dp),
        style = MaterialTheme.typography.titleLarge.copy(
            color = Color.White
        )
    )
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        if (list.isEmpty()) {
            items(count = 10) {
                MovieItem(movie = null) {
                    onClicked(it)
                }
            }
        } else {
            items(items = list) { item->
                MovieItem(movie = item) { string ->
                    onClicked(string)
                }
            }
        }

    }
}
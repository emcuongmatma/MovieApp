package com.movieapp.ui.movie_list.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.movieapp.R
import com.movieapp.domain.model.custom.CustomMovieModel

@Composable
fun MovieItem(
    movie: CustomMovieModel?,
    onClicked: (String) -> Unit
) {
    AsyncImage(
        model = if (movie == null) R.drawable.app_icon else movie.posterUrl,
        placeholder = painterResource(R.drawable.app_icon),
        contentDescription = null,
        modifier = Modifier
            .width(122.dp)
            .height(174.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                movie?.let {
                    onClicked(movie.slug.toString())
                }
                },
        contentScale = ContentScale.Crop
    )
}

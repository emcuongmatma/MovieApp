package com.movieapp.ui.movie_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.movieapp.R
import com.movieapp.data.model.custom.CustomMovieModel
import com.movieapp.ui.theme.netflix_gray_2
import com.movieapp.ui.theme.netflix_red

@Composable
fun MovieItem(
    movie: CustomMovieModel?,
    onItemSelected: (String, Int?) -> Unit
) {
    Column(
        modifier = Modifier
            .width(122.dp)
            .wrapContentHeight()
            .clip(RoundedCornerShape(8.dp)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(122.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            AsyncImage(
                model = if (movie!=null) ImageRequest.Builder(LocalContext.current)
                    .data(movie.posterUrl)
                    .crossfade(true)
                    .diskCachePolicy(CachePolicy.DISABLED)
                    .placeholder(R.drawable.movie_icon_placeholder)
                    .error(R.drawable.movie_icon_placeholder)
                    .build() else R.drawable.movie_icon_placeholder,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        movie?.let {
                            onItemSelected(movie.slug, movie.source)
                        }
                    },
                error = painterResource(R.drawable.app_icon)
            )
            movie?.let {
                if (movie.isResume == true) {
                    LinearProgressIndicator(
                        progress = {
                            if (movie.durationMs > 0)
                                movie.resumePositionMs / movie.durationMs.toFloat()
                            else 0f
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .clip(RoundedCornerShape(30.dp))
                            .align(Alignment.BottomCenter)
                            .background(color = netflix_gray_2),
                        color = netflix_red,
                        trackColor = netflix_gray_2,
                        strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                    )
                }
            }
        }
        movie?.let {
            Text(
                text = it.name.toString(),
                modifier = Modifier
                    .padding(top = 8.dp)
                    .alpha(0.8f),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

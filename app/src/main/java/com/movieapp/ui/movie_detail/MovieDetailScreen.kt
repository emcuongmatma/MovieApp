package com.movieapp.ui.movie_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.media3.ui.PlayerView
import com.movieapp.ui.movie_detail.components.CustomCircularProgress
import com.movieapp.ui.movie_detail.components.MovieDetails
import com.movieapp.ui.movie_detail.videoplayer.VideoPlayer
import com.movieapp.ui.theme.netflix_red
import com.movieapp.ui.theme.netflix_red2
import com.movieapp.ui.theme.netflix_white_15
import com.movieapp.ui.util.LoadStatus

@Composable
fun MovieDetailScreen(
    viewModel: MovieDetailViewModel,
    onPlayerViewReady: (PlayerView) -> Unit,
    onPIP: (Boolean) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val modifierCol =
        if (state.isFullScreen) Modifier
            .background(Color.Black)
            .fillMaxSize() else Modifier
            .background(Color.Black)
            .fillMaxSize()
            .statusBarsPadding()
    val modifierBox =
        if (state.isFullScreen) Modifier
            .background(Color.Black)
            .fillMaxSize() else Modifier
            .fillMaxWidth()
            .background(
                Color.Black
            )
    Column(
        modifier = modifierCol,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (state.status) {
            is LoadStatus.Loading -> {
                CustomCircularProgress()
            }

            is LoadStatus.Success -> {
                Box(
                    modifier = modifierBox
                ) {
                    VideoPlayer(
                        viewModel = viewModel,
                        onPlayerViewReady = onPlayerViewReady,
                        onPIP = onPIP
                    )
                }
                MovieDetails(
                    state,
                    onSeverSelected = { viewModel.onServerChange(it) },
                    onEpSelected = { viewModel.onEpChange(it) },
                    addFavMovie = { viewModel.addFavMovie() })
            }

            is LoadStatus.Error -> {
                viewModel.reset()
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        null,
                        tint = netflix_red2,
                        modifier = Modifier.size(50.dp)
                    )
                    Text(
                        "Lỗi kết nối !",
                        style = MaterialTheme.typography.bodyLarge.copy(color = netflix_white_15)
                    )
                    TextButton(
                        colors = ButtonDefaults.textButtonColors()
                            .copy(contentColor = netflix_red, containerColor = netflix_white_15),
                        onClick = {
                            viewModel.getMovieDetail()
                        }) { Text("Thử lại") }
                }

            }
            else -> {

            }

        }
    }
}



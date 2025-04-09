package com.movieapp.ui.movie_detail

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.movieapp.ui.movie_detail.composable.CustomCircularProgress
import com.movieapp.ui.movie_detail.composable.MovieDetails
import com.movieapp.ui.movie_detail.videoplayer.VideoPlayer
import com.movieapp.ui.theme.netflix_red
import com.movieapp.ui.theme.netflix_red2
import com.movieapp.ui.theme.netflix_white_15
import com.movieapp.ui.util.LoadStatus

@Composable
fun MovieDetailScreen(
    viewModel: MovieDetailViewModel,
    onExit: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val modifierCol =
        if (state.isFullScreen) Modifier
            .background(Color.Black)
            .fillMaxSize() else Modifier
            .background(Color.Black)
            .fillMaxSize()
            .statusBarsPadding()
    val modifierVideoPlayer = if (state.isFullScreen) Modifier.fillMaxSize() else Modifier
        .fillMaxWidth()
        .aspectRatio(16 / 9f)
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
                    modifier = modifierVideoPlayer.background(
                        Color.Black
                    )
                ) {
                    VideoPlayer(
                        modifier = modifierVideoPlayer,
                        viewModel = viewModel,
                        onExit = onExit
                    )
                }
                MovieDetails(
                    state,
                    onSeverSelected = { viewModel.onServerChange(it) },
                    onEpSelected = { viewModel.onEpChange(it) })
            }
            is LoadStatus.Error -> {
                Toast.makeText(LocalContext.current, state.status.description,Toast.LENGTH_SHORT).show()
                viewModel.reset()
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(imageVector =  Icons.Default.Info,null, tint = netflix_red2, modifier = Modifier.size(50.dp))
                    Text("Lỗi kết nối !", style = MaterialTheme.typography.bodyLarge.copy(color = netflix_white_15))
                    TextButton(
                        colors = ButtonDefaults.textButtonColors().copy(contentColor = netflix_red, containerColor = netflix_white_15),
                        onClick = {
                        viewModel.getMovieDetail()
                    }) {Text("Thử lại") }
                }
            }

        }
    }
}



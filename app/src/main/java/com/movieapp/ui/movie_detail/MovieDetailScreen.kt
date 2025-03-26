package com.movieapp.ui.movie_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.google.accompanist.systemuicontroller.SystemUiController
import com.movieapp.R
import com.movieapp.ui.movie_detail.composable.CustomCircularProgress
import com.movieapp.ui.movie_detail.composable.MovieDetails
import com.movieapp.ui.movie_detail.videoplayer.VideoPlayerTest

@Composable
fun MovieDetailScreen(
    viewModel: MovieDetailViewModel,
    onExit: () -> Unit,
    systemUiController: SystemUiController
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(state.isFullScreen) {
        systemUiController.isStatusBarVisible = !state.isFullScreen
    }
    val modifierCol =
        if (state.isFullScreen) Modifier
            .background(colorResource(R.color.netflix_black))
            .fillMaxSize() else Modifier
            .background(colorResource(R.color.netflix_black))
            .fillMaxSize()
            .statusBarsPadding()
    val modifierVideoPlayer = if (state.isFullScreen) Modifier.fillMaxSize() else Modifier
        .fillMaxWidth()
        .aspectRatio(16 / 9f)
    Column(
        modifier = modifierCol,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!state.isLoading) {
            Box(
                modifier = modifierVideoPlayer.background(
                    Color.Black
                )
            ) {
                VideoPlayerTest(
                    modifier = modifierVideoPlayer,
                    viewModel = viewModel,
                    onExit = onExit
                )
            }
            MovieDetails(
                state,
                onSeverSelected = { viewModel.onServerChange(it) },
                onEpSelected = { viewModel.onEpChange(it) })
        } else {
            CustomCircularProgress()
        }
    }


}


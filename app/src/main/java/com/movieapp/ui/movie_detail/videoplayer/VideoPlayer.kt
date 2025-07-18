@file:Suppress("DEPRECATION")

package com.movieapp.ui.movie_detail.videoplayer

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.movieapp.ui.movie_detail.MovieDetailViewModel


@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    viewModel: MovieDetailViewModel,
    onPlayerViewReady: (PlayerView) -> Unit,
    onPIP: (Boolean) -> Unit
) {
    var lifecycle by remember {
        mutableStateOf(Lifecycle.Event.ON_CREATE)
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val activity = context as? Activity
    val window = activity?.window
    val state by viewModel.state.collectAsState()
    LaunchedEffect(state.isPlaying, state.isFullScreen) {
        if (state.isFullScreen) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        if (state.isPlaying) {
            onPIP(true)
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            onPIP(false)
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            lifecycle = event
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
    BackHandler(
        enabled = state.isFullScreen
    ) {
        viewModel.isFullScreen(false)
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }
    AndroidView(
        factory = {
            PlayerView(it).also { it ->
                it.player = viewModel.player
                it.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                it.setFullscreenButtonState(state.isFullScreen)
                it.setFullscreenButtonClickListener {
                    activity?.requestedOrientation = if (state.isFullScreen) {
                        viewModel.isFullScreen(false)
                        ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                    } else {
                        viewModel.isFullScreen(true)
                        ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                    }
                }
                it.setShowNextButton(false)
                it.setShowPreviousButton(false)
                it.controllerShowTimeoutMs = 3000
            }
        },
        update = { playerView ->
            if (activity?.isInPictureInPictureMode == true) {
                playerView.hideController()
                playerView.useController = false
            } else {
                playerView.useController = true
                playerView.showController()
            }
            when (lifecycle) {
                Lifecycle.Event.ON_PAUSE -> {
                    playerView.onPause()
                }

                Lifecycle.Event.ON_RESUME -> {
                    playerView.onResume()
                    onPlayerViewReady(playerView)
                }

                Lifecycle.Event.ON_STOP -> {
                    viewModel.pausePlayer()
                    onPlayerViewReady(playerView)
                }

                else -> Unit
            }
        },
        modifier = if (state.isFullScreen) Modifier.fillMaxWidth() else Modifier
            .fillMaxWidth()
            .aspectRatio(16 / 9f)
    )

}




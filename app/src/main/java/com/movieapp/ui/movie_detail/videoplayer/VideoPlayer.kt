@file:Suppress("DEPRECATION")

package com.movieapp.ui.movie_detail.videoplayer

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
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
    modifier: Modifier = Modifier,
    viewModel: MovieDetailViewModel,
    onExit: () -> Unit
) {
    var lifecycle by remember {
        mutableStateOf(Lifecycle.Event.ON_CREATE)
    }
    val configuration = LocalConfiguration.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val activity = context as? Activity
    val window = activity?.window
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val state = viewModel.state.collectAsState()
    LaunchedEffect(isLandscape) {
        if (isLandscape) window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        else window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        viewModel.isFullScreen(isLandscape)
    }
    LaunchedEffect(state.value.isPlaying) {
        if (state.value.isPlaying){
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }else{
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
        enabled = isLandscape
    ) {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }
    Box(
        modifier = modifier.background(Color.Black)
    ) {
        AndroidView(
            factory = {
                PlayerView(context).also {
                    it.player = viewModel.player
                    it.resizeMode =
                        AspectRatioFrameLayout.RESIZE_MODE_FIT
                    it.setFullscreenButtonState(isLandscape)
                    it.setFullscreenButtonClickListener {
                        activity?.requestedOrientation = if (isLandscape) {
                            ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                        } else {
                            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                        }
                    }
                    it.setShowNextButton(false)
                    it.setShowPreviousButton(false)
                    it.controllerShowTimeoutMs = 3000
                }
            },
            update = {
                when (lifecycle) {
                    Lifecycle.Event.ON_PAUSE -> {
                        it.onPause()
                    }
                    Lifecycle.Event.ON_RESUME-> {
                        it.onResume()
                    }
                    else -> Unit
                }
            },
            modifier = modifier
        )
        if (!isLandscape) {
            IconButton(
                onClick = {
                    onExit()
                },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .background(Color.Transparent, shape = CircleShape)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "exit",
                    tint = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}
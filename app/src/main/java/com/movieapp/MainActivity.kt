package com.movieapp

import android.app.PictureInPictureParams
import android.app.PictureInPictureUiState
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Rational
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import com.movieapp.ui.HomeScreen
import com.movieapp.ui.theme.MovieappTheme
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var playerViewRef: PlayerView? = null

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            MovieappTheme {
                HomeScreen(
                    mainViewModel = hiltViewModel(),
                    onPlayerViewReady = { playerView ->
                        playerViewRef = playerView
                    },
                    onPIP = { isPlaying ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            if (isPlaying) {
                                // Bắt đầu phát → setup lại PIP
                                setupPipParams()
                            } else {
                                // Dừng hoặc tạm dừng → clear pip
                                clearPipParams()
                            }
                        }
                    })
            }
        }
    }

    @UnstableApi
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onPictureInPictureUiStateChanged(pipState: PictureInPictureUiState) {
        super.onPictureInPictureUiStateChanged(pipState)
        if (pipState.isTransitioningToPip) playerViewRef!!.hideController()
    }


    @RequiresApi(Build.VERSION_CODES.S)
    private fun setupPipParams() {
        val rect = Rect()
        playerViewRef!!.getGlobalVisibleRect(rect)
        val builder = PictureInPictureParams.Builder()
            .setAspectRatio(Rational(playerViewRef!!.width, playerViewRef!!.height))
            .setSourceRectHint(rect)
            .setSeamlessResizeEnabled(true)
            .setAutoEnterEnabled(true)
        setPictureInPictureParams(builder.build())
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun clearPipParams() {
        val empty = PictureInPictureParams.Builder().setAutoEnterEnabled(false).build()
        setPictureInPictureParams(empty)
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        val player = playerViewRef?.player
        if (player?.isPlaying == true && Build.VERSION.SDK_INT in Build.VERSION_CODES.O..Build.VERSION_CODES.R) {
            enterLegacyPipMode()
        }
    }

    private fun enterLegacyPipMode() {
        val playerView = playerViewRef ?: return
        val rect = Rect()
        playerView.getGlobalVisibleRect(rect)
        val params = PictureInPictureParams.Builder()
            .setAspectRatio(Rational(playerView.width, playerView.height))
            .setSourceRectHint(rect)
            .build()
        enterPictureInPictureMode(params)
    }
}

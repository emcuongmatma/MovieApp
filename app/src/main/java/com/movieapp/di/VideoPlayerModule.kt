package com.movieapp.di

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerNotificationManager
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.size.Scale
import coil3.toBitmap
import com.movieapp.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Module
@InstallIn(ViewModelComponent::class)
object VideoPlayerModule {
    @ViewModelScoped
    @Provides
    fun provideExoPlayer(@ApplicationContext context: Context): ExoPlayer {
        return ExoPlayer.Builder(context).build()
    }

    @ViewModelScoped
    @Provides
    fun provideMediaSession(
        @ApplicationContext context: Context,
        exoPlayer: ExoPlayer
    ): androidx.media3.session.MediaSession =
        androidx.media3.session.MediaSession.Builder(context, exoPlayer).setId("movie_session")
            .build()


    @UnstableApi
    @ViewModelScoped
    @Provides
    fun providePlayerNotificationManager(
        @ApplicationContext context: Context,
        mediaSession: androidx.media3.session.MediaSession,
        player: ExoPlayer
    ): PlayerNotificationManager {
        return PlayerNotificationManager.Builder(
            context,
            1, // notificationId
            "media_channel_id"
        )
            .setMediaDescriptionAdapter(object : PlayerNotificationManager.MediaDescriptionAdapter {
                override fun getCurrentContentTitle(player: Player): CharSequence =
                    player.currentMediaItem?.mediaMetadata?.title.toString()

                override fun getCurrentContentText(player: Player): CharSequence? =
                    player.currentMediaItem?.mediaMetadata?.description

                override fun getCurrentLargeIcon(
                    player: Player,
                    callback: PlayerNotificationManager.BitmapCallback
                ): Bitmap? {
                    val posterUrl = player.currentMediaItem?.mediaMetadata?.artworkUri
                    CoroutineScope(Dispatchers.IO).launch{
                        try {
                            val imageLoader = ImageLoader(context)
                            val request = ImageRequest.Builder(context)
                                .data(posterUrl)
                                .size(128)
                                .scale(Scale.FIT)
                                .allowHardware(false)
                                .build()
                            val drawable = imageLoader.execute(request).image!!.toBitmap()
                            callback.onBitmap(drawable)
                        }catch (_: Exception){
                        }
                    }
                    return BitmapFactory.decodeResource(context.resources, R.drawable.app_icon)
                }

                override fun createCurrentContentIntent(player: Player): PendingIntent? {
                    val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
                    return PendingIntent.getActivity(
                        context,
                        0,
                        launchIntent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                }
            })
            .build().apply {
                setMediaSessionToken(mediaSession.platformToken)
                setPlayer(player)
            }
    }
}
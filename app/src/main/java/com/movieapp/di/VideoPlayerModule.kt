package com.movieapp.di

import android.app.Application
import androidx.media3.exoplayer.ExoPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object VideoPlayerModule{
    @ViewModelScoped
    @Provides
    fun provideExoPlayer(app: Application): ExoPlayer {
        return  ExoPlayer.Builder(app).build()
    }
}
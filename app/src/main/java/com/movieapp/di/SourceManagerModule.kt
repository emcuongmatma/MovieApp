package com.movieapp.di

import com.movieapp.ui.util.MovieSourceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SourceManagerModule {
    @Provides
    @Singleton
    fun provideSourceManager(): MovieSourceManager = MovieSourceManager()
}
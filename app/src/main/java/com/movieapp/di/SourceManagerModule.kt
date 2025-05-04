package com.movieapp.di

import com.movieapp.data.datasource.remote.MovieSourceManager
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
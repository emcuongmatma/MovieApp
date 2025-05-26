package com.movieapp.di

import android.content.Context
import androidx.room.Room
import com.movieapp.data.datasource.local.MovieDataBase
import com.movieapp.data.datasource.local.dao.MovieDao
import com.movieapp.data.repository.local.LocalRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataBaseModule {
    @Provides
    @Singleton
    fun provideMovieDataBase(@ApplicationContext context: Context): MovieDataBase{
        return Room.databaseBuilder(
            context,
            MovieDataBase::class.java,
            "moviedb.db"
        ).build()
    }
    @Provides
    @Singleton
    fun provideMovieDao (movieDatabase: MovieDataBase): MovieDao =
        movieDatabase.getMovieDao()
    @Provides
    @Singleton
    fun provideMovieDaoRepo(movieDao: MovieDao): LocalRepositoryImpl =
        LocalRepositoryImpl(movieDao)
}
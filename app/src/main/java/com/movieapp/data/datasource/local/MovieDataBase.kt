package com.movieapp.data.datasource.local

import androidx.room.RoomDatabase
import com.movieapp.data.datasource.local.dao.MovieDao
import com.movieapp.data.datasource.local.typeconverter.MovieConverter
import com.movieapp.data.model.custom.CustomMovieModel

@androidx.room.TypeConverters(MovieConverter::class)
@androidx.room.Database(version = 1, entities = [CustomMovieModel::class], exportSchema = false)
abstract class MovieDataBase : RoomDatabase() {
    abstract fun getMovieDao(): MovieDao
}
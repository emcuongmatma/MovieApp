package com.movieapp.data.repository.local


import com.movieapp.data.model.custom.CustomMovieModel


interface LocalRepository{
    suspend fun favMovies(): List<CustomMovieModel>
    suspend fun reMovies(): List<CustomMovieModel>
    suspend fun remove(movie: CustomMovieModel)
}
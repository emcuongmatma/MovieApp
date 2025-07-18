package com.movieapp.data.repository.local

import com.movieapp.data.datasource.local.dao.MovieDao
import com.movieapp.data.model.custom.CustomMovieModel
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(
    private val movieDao: MovieDao
): LocalRepository {
    override suspend fun favMovies(): List<CustomMovieModel> {
        return movieDao.getAllFavMovie()
    }

    override suspend fun reMovies(): List<CustomMovieModel> {
        return movieDao.getAllResMovie()
    }

    override suspend fun remove(movie: CustomMovieModel) {
        movieDao.deleteMovie(movie)
    }

}
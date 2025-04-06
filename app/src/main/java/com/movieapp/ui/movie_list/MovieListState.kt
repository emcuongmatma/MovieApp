package com.movieapp.ui.movie_list

import com.movieapp.domain.model.custom.CustomMovieModel
import com.movieapp.domain.model.moviedetail.MovieDetailResponseModel
import com.movieapp.ui.util.LoadStatus
import com.movieapp.ui.util.MovieSourceManager
import com.movieapp.ui.util.Screen


data class MovieListState(
    val recentlyUpdateList: List<CustomMovieModel> = emptyList(),
    val newSeriesList:List<CustomMovieModel> = emptyList(),
    val newStandaloneFilmList:List<CustomMovieModel> = emptyList(),
    val newTvShowList:List<CustomMovieModel> = emptyList(),
    var movieSource: MovieSourceManager.MovieSource = MovieSourceManager.MovieSource.KKPhim,
    val movieSelected:MovieDetailResponseModel = MovieDetailResponseModel(),
    val status : LoadStatus = LoadStatus.Init(),
    val screen: Screen = Screen.HomeScreen,
    val isSourceManagerOpen: Boolean = false
)
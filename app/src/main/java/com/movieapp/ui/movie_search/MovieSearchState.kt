package com.movieapp.ui.movie_search

import com.movieapp.domain.model.custom.CustomMovieModel
import com.movieapp.ui.util.LoadStatus

data class MovieSearchState (
    val movieSearchList:List<CustomMovieModel> = emptyList(),
    val searchKey:String="",
    val status: LoadStatus = LoadStatus.Init()
)
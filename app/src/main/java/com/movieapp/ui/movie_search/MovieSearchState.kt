package com.movieapp.ui.movie_search

import com.movieapp.data.model.custom.CustomMovieModel
import com.movieapp.ui.util.LoadStatus

data class MovieSearchState (
    val recentlySearch:List<CustomMovieModel> = emptyList(),
    val searchKey:String="",
    val status: LoadStatus = LoadStatus.Init()
)
package com.movieapp.ui.movie_search

import com.movieapp.domain.model.custom.CustomMovieModel

data class MovieSearchState (
    val movieSearchList:List<CustomMovieModel> = emptyList(),
    val searchKey:String="",
    val isLoading:Boolean = false,
    val error:String? = null
)
package com.movieapp.ui.movie_detail

import com.movieapp.domain.model.moviedetail.MovieDetailResponseModel

data class MovieDetailState (
    val movie: MovieDetailResponseModel = MovieDetailResponseModel(),
    val serverSelected:Int =0,
    val epSelected:String="",
    val isLoading :Boolean = true,
    val error :String? = null,
    val isFullScreen:Boolean = false
)
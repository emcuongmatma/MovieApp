package com.movieapp.ui.movie_detail

import com.movieapp.data.datasource.local.dao.ResumeMovieDetail
import com.movieapp.data.model.moviedetail.MovieDetailResponseModel
import com.movieapp.ui.util.LoadStatus

data class MovieDetailState (
    val movie: MovieDetailResponseModel = MovieDetailResponseModel(),
    val serverSelected:Int =0,
    val epSelected:String="",
    val status: LoadStatus = LoadStatus.Loading(),
    val slug:String="",
    val isFullScreen:Boolean = false,
    val isPlaying: Boolean = false,
    val resume: ResumeMovieDetail = ResumeMovieDetail("",0,0),
    val isFav: Boolean = false
)
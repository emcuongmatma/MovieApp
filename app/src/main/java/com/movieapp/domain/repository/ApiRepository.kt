package com.movieapp.domain.repository

import com.movieapp.domain.model.recentlyupdate.RecentlyUpdateMovieResponseModel
import com.movieapp.domain.model.custom.CustomMovieResponseModel
import com.movieapp.domain.model.moviedetail.MovieDetailResponseModel
import com.skydoves.sandwich.ApiResponse


interface ApiRepository {
    suspend fun getRecentlyUpdateMovie():ApiResponse<RecentlyUpdateMovieResponseModel>
    suspend fun getMovieDetail(name: String):ApiResponse<MovieDetailResponseModel>
    suspend fun getCustomMovie(type:String):ApiResponse<CustomMovieResponseModel>
    suspend fun getMovieDetailByName(name:String):ApiResponse<CustomMovieResponseModel>
}
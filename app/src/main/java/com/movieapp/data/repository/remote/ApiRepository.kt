package com.movieapp.data.repository.remote

import com.movieapp.data.model.custom.CustomMovieResponseModel
import com.movieapp.data.model.moviedetail.MovieDetailResponseModel
import com.movieapp.data.model.nCModel.NCMovieDetailResponeModel
import com.movieapp.data.model.recentlyupdate.RecentlyUpdateMovieResponseModel
import com.skydoves.sandwich.ApiResponse


interface ApiRepository {
    suspend fun getRecentlyUpdateMovie(page:Int):ApiResponse<RecentlyUpdateMovieResponseModel>
    suspend fun getMovieDetail(name: String):ApiResponse<MovieDetailResponseModel>
    suspend fun getMovieDetailNC(name: String):ApiResponse<NCMovieDetailResponeModel>
    suspend fun getCustomMovie(type:String,page:Int,country:String):ApiResponse<CustomMovieResponseModel>
    suspend fun getMovieDetailByName(name:String,page:Int):ApiResponse<CustomMovieResponseModel>
}
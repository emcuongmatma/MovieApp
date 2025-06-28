package com.movieapp.data.repository.remote

import com.movieapp.data.model.nCModel.NCMovieDetailResponeModel
import com.movieapp.data.model.recentlyupdate.RecentlyUpdateMovieResponseModel
import com.movieapp.data.model.custom.CustomMovieResponseModel
import com.movieapp.data.model.moviedetail.MovieDetailResponseModel
import com.movieapp.data.datasource.remote.MovieSourceManager
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.ktor.getApiResponse
import io.ktor.client.HttpClient

class ApiRepositoryImpl(
    private val httpClient: HttpClient,
    private val movieSourceManager: MovieSourceManager
):ApiRepository {
    override suspend fun getRecentlyUpdateMovie(page:Int): ApiResponse<RecentlyUpdateMovieResponseModel> =
        httpClient.getApiResponse(urlString = movieSourceManager.currentSource.value.ADD_RECENTLY_URL+"?page=${page}")
    override suspend fun getMovieDetail(name:String): ApiResponse<MovieDetailResponseModel> =
        httpClient.getApiResponse(urlString = movieSourceManager.currentSource.value.GET_DETAIL_URL+ name)
    override suspend fun getMovieDetailNC(name: String): ApiResponse<NCMovieDetailResponeModel> =
        httpClient.getApiResponse(urlString = movieSourceManager.currentSource.value.GET_DETAIL_URL+ name)
    override suspend fun getCustomMovie(type:String,page:Int,country:String): ApiResponse<CustomMovieResponseModel> =
        httpClient.getApiResponse(urlString = movieSourceManager.currentSource.value.GET_CUSTOM_HEAD + type +"?&country=${country}"+movieSourceManager.currentSource.value.SEARCH_TAIL+"&page=${page}")
    override suspend fun getMovieDetailByName(name: String,page:Int): ApiResponse<CustomMovieResponseModel> =
        httpClient.getApiResponse(urlString = movieSourceManager.currentSource.value.SEARCH_HEAD + name + movieSourceManager.currentSource.value.SEARCH_TAIL+"&page=${page}")
}
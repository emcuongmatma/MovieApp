package com.movieapp.data.datasource.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.movieapp.data.datasource.remote.MovieSourceManager
import com.movieapp.data.model.custom.CustomMovieModel
import com.movieapp.data.repository.remote.ApiRepository
import com.movieapp.ui.util.toListMovie
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onFailure
import com.skydoves.sandwich.onSuccess
import javax.inject.Inject

class SearchPagingSource @Inject constructor(
    private val apiRepository: ApiRepository,
    private val movieSourceManager: MovieSourceManager,
    private val name:String
): PagingSource<Int, CustomMovieModel>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CustomMovieModel> {
        return try {
            val currentPage = params.key ?: 1
            var result = listOf<CustomMovieModel>()
            apiRepository.getMovieDetailByName(name, currentPage)
                .onSuccess {
                    result = data.toListMovie(
                        movieSourceManager
                    )
                }
                .onError {
                    throw Exception(this.payload.toString())
                }
                .onFailure {
                    throw Exception(this.message())
                }
            LoadResult.Page(
                data = result,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (result.isEmpty()) null else currentPage + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CustomMovieModel>): Int? = null
}
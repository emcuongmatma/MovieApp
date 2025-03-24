package com.movieapp.ui.movie_search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movieapp.domain.model.custom.CustomMovieModel
import com.movieapp.domain.repository.ApiRepository
import com.movieapp.ui.util.MovieSourceManager
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onFailure
import com.skydoves.sandwich.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieSearchViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val movieSourceManager: MovieSourceManager
):ViewModel() {
    private val _state = MutableStateFlow(MovieSearchState())
    val state = _state.asStateFlow()
    private var job : Job? = null
    private fun getMovieDetailByName() =
        viewModelScope.launch(Dispatchers.IO) {
            apiRepository.getMovieDetailByName(_state.value.searchKey)
                .onSuccess {
                    _state.update {
                        Log.e("13", data.data!!.items!!.toString())
                        it.copy(
                            isLoading = false,
                            movieSearchList = data.data!!.items!!.converter()
                        )
                    }
                }
                .onError {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = this.payload.toString()
                        )
                    }
                }
                .onFailure {
                    Log.e("log", this.message())
                }
        }

    fun updateSearchKey(key: String) {
        _state.update {
            it.copy(
                searchKey = key,
                isLoading = true
            )
        }
        job?.cancel()
        job = getMovieDetailByName()
    }
    private fun List<CustomMovieModel>.converter(): List<CustomMovieModel> {
        val list: List<CustomMovieModel> = when (movieSourceManager.currentSource.value) {
            is MovieSourceManager.MovieSource.KKPhim -> {
                this.map { item ->
                    item.copy(posterUrl = if (!item.posterUrl!!.contains("https")) movieSourceManager.currentSource.value.IMAGE_BASE_URL + item.posterUrl else item.posterUrl)
                }
            }
            is MovieSourceManager.MovieSource.Ophim -> {
                this.map { item ->
                    item.copy(posterUrl = if (!item.posterUrl!!.contains("https")) movieSourceManager.currentSource.value.IMAGE_BASE_URL + item.thumbUrl else item.thumbUrl)
                }
            }
            is MovieSourceManager.MovieSource.NguonC -> {
                return this
            }
        }
        return list
    }
}
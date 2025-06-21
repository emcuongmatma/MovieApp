package com.movieapp.ui.movie_search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movieapp.data.repository.remote.ApiRepository
import com.movieapp.ui.util.LoadStatus
import com.movieapp.data.datasource.remote.MovieSourceManager
import com.movieapp.ui.util.toListMovie
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
) : ViewModel() {
    private val _state = MutableStateFlow(MovieSearchState())
    val state = _state.asStateFlow()
    private var job: Job? = null
    private fun getMovieDetailByName() =
        viewModelScope.launch(Dispatchers.IO) {
            apiRepository.getMovieDetailByName(_state.value.searchKey)
                .onSuccess {
                    _state.update {
                        it.copy(
                            movieSearchList = data.toListMovie(movieSourceManager),
                            status = LoadStatus.Success()
                        )
                    }
                }
                .onError {
                    setToast(this.payload.toString())
                }
                .onFailure {
                    setToast(this.message())
                }
        }

    fun updateSearchKey(key: String) {
        _state.update {
            it.copy(
                searchKey = key
            )
        }
        job?.cancel()
        _state.update { it.copy(status = LoadStatus.Loading()) }
        job =  getMovieDetailByName()
    }

    fun setToast(err: String) {
        _state.update {
            it.copy(
                status = LoadStatus.Error(err)
            )
        }
    }
}
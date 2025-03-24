package com.movieapp.ui.movie_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movieapp.domain.model.custom.CustomMovieModel
import com.movieapp.domain.model.custom.DataModel
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
class MovieListViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val movieSourceManager: MovieSourceManager
) : ViewModel() {
    private val _state = MutableStateFlow(MovieListState())
    val state = _state.asStateFlow()
    private var fetch1: Job? = null
    private var fetch2: Job? = null
    private var fetch3: Job? = null
    private var fetch4: Job? = null

    init {
        observeMovieSource()
        _state.update { it.copy(isLoading = true) }
    }

    private fun observeMovieSource() {
        viewModelScope.launch {
            movieSourceManager.currentSource.collect { newSource ->
                _state.value = _state.value.copy(
                    movieSource = newSource
                )
            fetchAllMovies()
            }
        }
    }
    private fun fetchAllMovies() {
        fetch1?.cancel()
        fetch2?.cancel()
        fetch3?.cancel()
        fetch4?.cancel()
        fetch1 = getRecentlyUpdate()
        fetch2 = getCustomMovie("phim-bo"){ data->
            _state.value = _state.value.copy(newSeriesList = data.items!!.converter())
        }
        fetch3 = getCustomMovie("phim-le"){ data->
            _state.value = _state.value.copy(newStandaloneFilmList = data.items!!.converter())}
        fetch4 = getCustomMovie("tv-shows"){ data->
            _state.value = _state.value.copy(newTvShowList = data.items!!.converter())}
    }
    private fun clearList() {
        _state.update {
            it.copy(
                recentlyUpdateList = emptyList(),
                newSeriesList = emptyList(),
                newStandaloneFilmList = emptyList(),
                newTvShowList = emptyList()
            )
        }
    }
    private fun getRecentlyUpdate() =
        viewModelScope.launch(Dispatchers.IO) {
            apiRepository.getRecentlyUpdateMovie()
                .onSuccess {
                    _state.update {
                        it.copy(
                            recentlyUpdateList = data.items.orEmpty().converter()
                        )
                    }
                }
                .onError {
                    _state.update {
                        it.copy(
                            error = this.payload.toString()
                        )
                    }
                }
                .onFailure {
                    Log.e("log", this.message())
                }
        }


    private fun getCustomMovie(type: String,onUpdate:(DataModel)->Unit) =
        viewModelScope.launch(Dispatchers.IO) {
            apiRepository.getCustomMovie(type)
                .onSuccess {
                    onUpdate(data.data!!)
                }
                .onError {
                    _state.update {
                        it.copy(
                            error = this.payload.toString()
                        )
                    }
                }
                .onFailure {
                    Log.e("log", this.message())
                }
        }


    fun changeSource(index: Int) {
        when (index) {
            0 -> {
                movieSourceManager.changeSource(MovieSourceManager.MovieSource.KKPhim)
            }

            1 -> {
                movieSourceManager.changeSource(MovieSourceManager.MovieSource.Ophim)
            }

            2 -> {
                movieSourceManager.changeSource(MovieSourceManager.MovieSource.NguonC)
            }
        }
        clearList()
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
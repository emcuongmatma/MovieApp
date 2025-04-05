package com.movieapp.ui.movie_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movieapp.domain.model.custom.CustomMovieResponseModel
import com.movieapp.domain.repository.ApiRepository
import com.movieapp.ui.util.LoadStatus
import com.movieapp.ui.util.MovieSourceManager
import com.movieapp.ui.util.converter
import com.movieapp.ui.util.filter
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
        _state.update { it.copy(status = LoadStatus.Loading()) }
        observeMovieSource()
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
        fetch2 = getCustomMovie("phim-bo") { data ->
            _state.value = _state.value.copy(
                newSeriesList = data.toListMovie(movieSourceManager)
            )
        }
        fetch3 = getCustomMovie("phim-le") { data ->
            _state.value = _state.value.copy(
                newStandaloneFilmList = data.toListMovie(movieSourceManager)
            )
        }
        fetch4 = getCustomMovie("tv-shows") { data ->
            _state.value = _state.value.copy(
                newTvShowList = data.toListMovie(movieSourceManager),
                status = LoadStatus.Success()
            )
        }
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
                            recentlyUpdateList = data.items.orEmpty().filter()
                                .converter(movieSourceManager)
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


    private fun getCustomMovie(type: String, onUpdate: (CustomMovieResponseModel) -> Unit) =
        viewModelScope.launch(Dispatchers.IO) {
            apiRepository.getCustomMovie(type)
                .onSuccess {
                    onUpdate(data)
                }
                .onError {
                    setToast(this.payload.toString())
                }
                .onFailure {
                    setToast(this.message())
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
    fun setToast(err:String){
        _state.update {
            it.copy(
                status = LoadStatus.Error(err)
            )
        }
    }
    fun reset(){
        _state.update {
            it.copy(
                status = LoadStatus.Init()
            )
        }
    }
}

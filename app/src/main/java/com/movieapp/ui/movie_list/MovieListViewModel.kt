package com.movieapp.ui.movie_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movieapp.domain.repository.ApiRepository
import com.movieapp.ui.util.LoadStatus
import com.movieapp.ui.util.MovieSourceManager
import com.movieapp.ui.util.Screen
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
        fetch2 = getCustomMovie("phim-bo",_state.value.currentPageS)
        fetch3 = getCustomMovie("phim-le",_state.value.currentPageF)
        fetch4 = getCustomMovie("tv-shows",_state.value.currentPageT)
    }

    private fun clearList() {
        _state.update {
            it.copy(
                recentlyUpdateList = emptyList(),
                newSeriesList = emptyList(),
                newStandaloneFilmList = emptyList(),
                newTvShowList = emptyList(),
                currentPageS = 1,
                currentPageR = 1,
                currentPageF = 1,
                currentPageT = 1
            )
        }
    }

    private fun getRecentlyUpdate() =
        viewModelScope.launch(Dispatchers.IO) {
            apiRepository.getRecentlyUpdateMovie(_state.value.currentPageR)
                .onSuccess {
                    _state.update {
                        it.copy(
                            recentlyUpdateList = _state.value.recentlyUpdateList + data.items.orEmpty()
                                .filter()
                                .converter(movieSourceManager),
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


    private fun getCustomMovie(type: String,currentPage:Int) =
        viewModelScope.launch(Dispatchers.IO) {
            apiRepository.getCustomMovie(type,currentPage)
                .onSuccess {
                    when (type) {
                        "phim-bo" -> _state.value = _state.value.copy(
                            newSeriesList = _state.value.newSeriesList + data.toListMovie(
                                movieSourceManager
                            )
                        )

                        "phim-le" -> _state.value = _state.value.copy(
                            newStandaloneFilmList = _state.value.newStandaloneFilmList + data.toListMovie(
                                movieSourceManager
                            )
                        )

                        "tv-shows" -> _state.value = _state.value.copy(
                            newTvShowList = _state.value.newTvShowList + data.toListMovie(
                                movieSourceManager
                            )
                        )
                    }
                    _state.value = _state.value.copy(status = LoadStatus.Success())
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
    private fun setToast(err: String) {
        _state.update {
            it.copy(
                status = LoadStatus.Error(err)
            )
        }
    }
    fun setScreen(screen: Screen) {
        _state.update { it.copy(screen = screen) }
    }
    fun setSourceManagerOpen(boolean: Boolean) {
        _state.update {
            it.copy(
                isSourceManagerOpen = boolean
            )
        }
    }
    fun setTypeSlug(slug: String) {
        _state.update {
            it.copy(
                typeSlug = slug
            )
        }
    }
    fun getMoreResult() {
        _state.value = _state.value.copy(status = LoadStatus.Loading())
        when (_state.value.typeSlug) {
            "phim-moi-cap-nhat" -> {
                _state.value = _state.value.copy(currentPageR = _state.value.currentPageR + 1)
                getRecentlyUpdate()
            }
            "phim-bo" -> {
                _state.value = _state.value.copy(currentPageS = _state.value.currentPageS + 1)
                getCustomMovie("phim-bo",_state.value.currentPageS)
            }
            "phim-le" -> {
                _state.value = _state.value.copy(currentPageF = _state.value.currentPageF + 1)
                getCustomMovie("phim-le",_state.value.currentPageF)
            }
            "tv-shows" -> {
                _state.value = _state.value.copy(currentPageT = _state.value.currentPageT + 1)
                getCustomMovie("tv-shows",_state.value.currentPageT)
            }
            else -> {
                setToast("Lỗi ${_state.value.typeSlug}")
            }
        }
    }

    fun isGridListOpen(boolean: Boolean) {
        _state.update { it.copy(isOpenGridList = boolean) }
    }
    fun reset() {
        _state.update {
            it.copy(
                status = LoadStatus.Init()
            )
        }
    }
}

package com.movieapp.ui.movie_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movieapp.data.datasource.local.dao.MovieDao
import com.movieapp.data.datasource.remote.MovieSourceManager
import com.movieapp.data.model.custom.CustomMovieModel
import com.movieapp.data.repository.remote.ApiRepository
import com.movieapp.ui.util.LoadStatus
import com.movieapp.ui.util.Screen
import com.movieapp.ui.util.converter
import com.movieapp.ui.util.toListMovie
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val movieSourceManager: MovieSourceManager,
    private val movieDao: MovieDao
) : ViewModel() {
    private val _state = MutableStateFlow(MovieListState())
    val state = _state.asStateFlow()
    private var fetchJob: Job? = null

    init {
        _state.update { it.copy(status = LoadStatus.Loading()) }
        observeMovieSource()
    }

    private fun observeMovieSource() {
        viewModelScope.launch {
            movieSourceManager.currentSource.collect { newSource ->
                _state.update {
                    it.copy(
                        movieSource = newSource
                    )
                }
                fetchAllMovies()
            }
        }
    }

    fun fetchAllMovies() {
        fetchJob?.cancel()
        clearList()
        _state.update { it.copy(status = LoadStatus.Loading(), isRefreshing = true) }
        fetchJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val recentList = async { getRecentlyUpdate(_state.value.currentPageR) }.await()
                val phimBoList =
                    async { getCustomMovie("phim-bo", _state.value.currentPageS) }.await()
                val phimLeList =
                    async { getCustomMovie("phim-le", _state.value.currentPageF) }.await()
                val tvList = async { getCustomMovie("tv-shows", _state.value.currentPageT) }.await()
                _state.update {
                    it.copy(
                        recentlyUpdateList = it.recentlyUpdateList + recentList,
                        newSeriesList = it.newSeriesList + phimBoList,
                        newStandaloneFilmList = it.newStandaloneFilmList + phimLeList,
                        newTvShowList = it.newTvShowList + tvList,
                        isRefreshing = false,
                        status = LoadStatus.Success()
                    )
                }
            } catch (e: CancellationException) {
                println("FetchAllMovies bị huỷ $e")
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        status = LoadStatus.Error(
                            e.message ?: "Lỗi không xác định"
                        )
                    )
                }
            }
        }
    }

    fun loadFavMovies() {
        _state.update { it.copy(isRefreshing = true, status = LoadStatus.Loading()) }
        viewModelScope.launch(Dispatchers.IO) {
            _state.update {
                it.copy(
                    resMovieList = movieDao.getAllResMovie().reversed(),
                    favMovieList = movieDao.getAllFavMovie().reversed(),
                    isRefreshing = false,
                    status = LoadStatus.Success()
                )
            }
        }
    }

    private suspend fun getRecentlyUpdate(currentPage: Int): List<CustomMovieModel> {
        var resultList: List<CustomMovieModel> = emptyList()
        apiRepository.getRecentlyUpdateMovie(currentPage)
            .onSuccess {
                resultList =
                    data.items.orEmpty()
                        .converter(movieSourceManager)
            }
            .onError {
                setToast(this.payload.toString())
            }
        return resultList
    }

    private suspend fun getCustomMovie(type: String, currentPage: Int): List<CustomMovieModel> {
        var resultList: List<CustomMovieModel> = emptyList()
        apiRepository.getCustomMovie(type, currentPage)
            .onSuccess {
                resultList = data.toListMovie(
                    movieSourceManager
                )
            }
            .onError {
                setToast(this.payload.toString())
            }
        return resultList
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
    }

    fun getMoreResult() {
        _state.update { it.copy(status = LoadStatus.Loading()) }
        viewModelScope.launch(Dispatchers.IO) {
            when (_state.value.typeSlug) {
                "phim-moi-cap-nhat" -> {
                    _state.update {
                        it.copy(
                            recentlyUpdateList = it.recentlyUpdateList + getRecentlyUpdate(_state.value.currentPageR+1),
                            currentPageR = _state.value.currentPageR + 1,
                            status = LoadStatus.Success()
                        )
                    }
                }

                "phim-bo" -> {
                    _state.update {
                        it.copy(
                            newSeriesList = it.newSeriesList + getCustomMovie("phim-bo", _state.value.currentPageS+1),
                            currentPageS = _state.value.currentPageS + 1,
                            status = LoadStatus.Success()
                        )
                    }
                }

                "phim-le" -> {
                    _state.update {
                        it.copy(
                            newStandaloneFilmList = it.newStandaloneFilmList + getCustomMovie("phim-le", _state.value.currentPageF+1),
                            currentPageF = _state.value.currentPageF + 1,
                            status = LoadStatus.Success()
                        )
                    }
                }
                "tv-shows" -> {
                    _state.update {
                        it.copy(
                            newTvShowList = it.newTvShowList + getCustomMovie("tv-shows", _state.value.currentPageT+1),
                            currentPageT = _state.value.currentPageT + 1,
                            status = LoadStatus.Success()
                        )
                    }
                }

                else -> {
                    setToast("Lỗi ${_state.value.typeSlug}")
                }
            }
        }
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

    fun onClear(string: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (string) {
                "r" -> {
                    movieDao.delRnF()
                    movieDao.updateRaF()
                }

                "f" -> {
                    movieDao.delFnR()
                    movieDao.updateFaR()
                }
            }
            loadFavMovies()
        }
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
        if (screen is Screen.FavouriteScreen) loadFavMovies()
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

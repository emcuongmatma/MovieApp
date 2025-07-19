package com.movieapp.ui.movie_list

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.movieapp.data.datasource.local.dao.MovieDao
import com.movieapp.data.datasource.paging.PagingSource
import com.movieapp.data.datasource.remote.MovieSourceManager
import com.movieapp.data.model.custom.CustomMovieModel
import com.movieapp.data.repository.remote.ApiRepository
import com.movieapp.ui.BaseViewModel
import com.movieapp.ui.UiEvent
import com.movieapp.ui.util.LoadStatus
import com.movieapp.ui.util.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val movieSourceManager: MovieSourceManager,
    private val movieDao: MovieDao
) : BaseViewModel() {
    private val _state = MutableStateFlow(MovieListState())
    val state = _state.asStateFlow()
    private val _pagingSeriesKR = MutableStateFlow(PagingData.empty<CustomMovieModel>())
    val pagingSeriesKR = _pagingSeriesKR.asStateFlow()

    private val _pagingSeriesCN = MutableStateFlow(PagingData.empty<CustomMovieModel>())
    val pagingSeriesCN = _pagingSeriesCN.asStateFlow()

    private val _pagingSeriesUSUK = MutableStateFlow(PagingData.empty<CustomMovieModel>())
    val pagingSeriesUSUK = _pagingSeriesUSUK.asStateFlow()

    private val _pagingMovies = MutableStateFlow(PagingData.empty<CustomMovieModel>())
    val pagingMovies = _pagingMovies.asStateFlow()

    private val _pagingTVS = MutableStateFlow(PagingData.empty<CustomMovieModel>())
    val pagingTVS = _pagingTVS.asStateFlow()
    private var fetchJob: Job? = null

    init {
        fetchAllMovies()
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
                val krJob = async { getCustomMovie("phim-bo", "han-quoc").first() }
                val cnJob = async { getCustomMovie("phim-bo", "trung-quoc").first() }
                val usukJob = async { getCustomMovie("phim-bo", "au-my").first() }
                val leJob = async { getCustomMovie("phim-le", "").first() }
                val tvsJob = async { getCustomMovie("tv-shows", "").first() }
                _pagingSeriesKR.value = krJob.await()
                _pagingSeriesCN.value = cnJob.await()
                _pagingSeriesUSUK.value = usukJob.await()
                _pagingMovies.value = leJob.await()
                _pagingTVS.value = tvsJob.await()
                _state.update {
                    it.copy(
                        isRefreshing = false,
                        status = LoadStatus.Success()
                    )
                }
            } catch (e: CancellationException) {
                println("FetchAllMovies bị huỷ $e")
            } catch (e: Exception) {
                setToast(e.message.toString())
                _state.update {
                    it.copy(
                        isRefreshing = false,
                        status = LoadStatus.Error(e.message ?: "Lỗi không xác định")
                    )
                }
            }
        }
    }

    private fun getCustomMovie(
        type: String,
        country: String
    ): Flow<PagingData<CustomMovieModel>> {
        return Pager(PagingConfig(pageSize = movieSourceManager.currentSource.value.itemPerPage)) {
            PagingSource(
                apiRepository = apiRepository,
                movieSourceManager = movieSourceManager,
                type,
                country
            )
        }.flow.cachedIn(viewModelScope)
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
    private fun clearList() {
        _pagingSeriesKR.value = PagingData.empty()
        _pagingSeriesCN.value = PagingData.empty()
        _pagingSeriesUSUK.value = PagingData.empty()
        _pagingMovies.value = PagingData.empty()
        _pagingTVS.value = PagingData.empty()
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


    fun setToast(err: String) {
        sendEvent(UiEvent.ShowToast(err))
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


}

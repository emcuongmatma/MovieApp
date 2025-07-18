package com.movieapp.ui.movie_search

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.movieapp.data.datasource.local.dao.MovieDao
import com.movieapp.data.datasource.paging.SearchPagingSource
import com.movieapp.data.datasource.remote.MovieSourceManager
import com.movieapp.data.model.custom.CustomMovieModel
import com.movieapp.data.repository.remote.ApiRepository
import com.movieapp.ui.BaseViewModel
import com.movieapp.ui.UiEvent
import com.movieapp.ui.util.LoadStatus
import com.movieapp.ui.util.fixImg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class MovieSearchViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val movieSourceManager: MovieSourceManager,
    private val movieDao: MovieDao
) : BaseViewModel() {
    private val _state = MutableStateFlow(MovieSearchState())
    val state = _state.asStateFlow()
    private val _pagingFlow = MutableStateFlow(PagingData.empty<CustomMovieModel>())
    val pagingFLow = _pagingFlow.asStateFlow()
    private var job: Job? = null
    init {
        observeSearchQuery()
        observeRecentlySearchList()
    }
    private fun observeSearchQuery() {
        _state
            .map { it.searchKey }
            .debounce(300)
            .distinctUntilChanged()
            .onEach { query ->
                if (query.isBlank()) {
                    _state.update { it.copy(status = LoadStatus.Init()) }
                } else {
                    job?.cancel()
                    _state.update { it.copy(status = LoadStatus.Loading()) }
                    _pagingFlow.value = PagingData.empty()
                    job = getMovieDetailByName()
                }
            }
            .launchIn(viewModelScope)
    }
    private fun observeRecentlySearchList(){
        viewModelScope.launch(context = Dispatchers.IO) {
            movieDao.getRecentlySearch().collect { recentlySearch->
                _state.update { it.copy(recentlySearch = recentlySearch) }
            }
        }
    }

private fun getMovieDetailByName() =
    Pager(PagingConfig(pageSize = 24)) {
        SearchPagingSource(apiRepository = apiRepository, movieSourceManager = movieSourceManager, name = _state.value.searchKey)
    }.flow.cachedIn(viewModelScope)
        .onEach { pagingData ->
            _pagingFlow.value = pagingData
            _state.update {
                it.copy(
                    status = LoadStatus.Success()
                )
            }
        }
        .launchIn(viewModelScope)


    fun addSearchHistory(movie: CustomMovieModel){
        viewModelScope.launch {
            if (!movie.slug.isEmpty()) {
                movieDao.insert(
                    movie.copy(
                        source = movieSourceManager.currentSource.value.index,
                        isRecentlySearch = true
                    ).fixImg(movieSourceManager)
                )
            }
        }
    }

    fun updateSearchKey(key: String) {
        _state.update {
            it.copy(
                searchKey = key
            )
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
}

//    private fun getMovieDetailByName() =
//        viewModelScope.launch(Dispatchers.IO) {
//            apiRepository.getMovieDetailByName(_state.value.searchKey,_state.value.currentPage)
//                .onSuccess {
//                    if (data.items!!.isEmpty()&& data.data!!.items!!.isEmpty()){
//                        _state.update { it.copy(status = LoadStatus.Error("No more results")) }
//                    }else{
//                        _state.update {
//                            it.copy(
//                                movieSearchList = it.movieSearchList + data.toListMovie(movieSourceManager),
//                                status = LoadStatus.Success()
//                            )
//                        }
//                    }
//                }
//                .onError {
//                    setToast(this.payload.toString())
//
//                }
//                .onFailure {
//                    setToast(this.message())
//                }
//        }
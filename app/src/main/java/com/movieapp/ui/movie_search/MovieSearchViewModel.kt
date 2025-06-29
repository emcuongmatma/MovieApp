package com.movieapp.ui.movie_search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movieapp.data.datasource.local.dao.MovieDao
import com.movieapp.data.datasource.remote.MovieSourceManager
import com.movieapp.data.model.custom.CustomMovieModel
import com.movieapp.data.repository.remote.ApiRepository
import com.movieapp.ui.util.LoadStatus
import com.movieapp.ui.util.fixImg
import com.movieapp.ui.util.toListMovie
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onFailure
import com.skydoves.sandwich.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class MovieSearchViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val movieSourceManager: MovieSourceManager,
    private val movieDao: MovieDao
) : ViewModel() {
    private val _state = MutableStateFlow(MovieSearchState())
    val state = _state.asStateFlow()
    private var job: Job? = null
    init {
        getRecentlySearch()
        viewModelScope.launch {
            _state
                .map { it.searchKey }
                .debounce(300)
                .distinctUntilChanged()
                .collectLatest { query ->
                    if (query.isBlank()) {
                        _state.update { it.copy(status = LoadStatus.Init()) }
                    } else {
                        job?.cancel()
                        _state.update { it.copy(status = LoadStatus.Loading(), currentPage = 1, movieSearchList = listOf()) }
                        job = getMovieDetailByName()
                    }
                }
        }
    }
    private fun getRecentlySearch(){
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(recentlySearch = movieDao.getRecentlySearch()) }
        }
    }
    private fun getMovieDetailByName() =
        viewModelScope.launch(Dispatchers.IO) {
            apiRepository.getMovieDetailByName(_state.value.searchKey,_state.value.currentPage)
                .onSuccess {
                    if (data.items!!.isEmpty()&& data.data!!.items!!.isEmpty()){
                        _state.update { it.copy(status = LoadStatus.Error("No more results")) }
                    }else{
                        _state.update {
                            it.copy(
                                movieSearchList = it.movieSearchList + data.toListMovie(movieSourceManager),
                                status = LoadStatus.Success()
                            )
                        }
                    }
                }
                .onError {
                    setToast(this.payload.toString())

                }
                .onFailure {
                    setToast(this.message())
                }
        }

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

    fun onMoreResult(){
        _state.update { it.copy(currentPage = _state.value.currentPage+1, status = LoadStatus.Loading()) }
        getMovieDetailByName()
    }

    fun updateSearchKey(key: String) {
        _state.update {
            it.copy(
                searchKey = key
            )
        }
    }

    fun setToast(err: String) {
        _state.update {
            it.copy(
                status = LoadStatus.Error(err)
            )
        }
    }
}
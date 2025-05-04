package com.movieapp.ui.movie_detail

import android.util.Log
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.movieapp.data.datasource.local.dao.MovieDao
import com.movieapp.data.datasource.local.dao.ResumeMovieDetail
import com.movieapp.data.datasource.remote.MovieSourceManager
import com.movieapp.data.model.moviedetail.MovieDetailModel
import com.movieapp.data.model.moviedetail.MovieDetailResponseModel
import com.movieapp.data.model.moviedetail.toCustomMovieModel
import com.movieapp.data.model.nCModel.toMovieDetailResponseModel
import com.movieapp.data.repository.remote.ApiRepository
import com.movieapp.ui.util.LoadStatus
import com.movieapp.ui.util.fixImg
import com.skydoves.sandwich.isSuccess
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onFailure
import com.skydoves.sandwich.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MovieDetailViewModel
@Inject constructor(
    private val apiRepository: ApiRepository,
    val player: ExoPlayer,
    private val movieSourceManager: MovieSourceManager,
    private val movieDao: MovieDao
) : ViewModel() {
    private val _state = MutableStateFlow(MovieDetailState())
    val state = _state.asStateFlow()
    private lateinit var videoItems: MutableList<List<MediaItem>>

    init {
        player.prepare()
        player.playWhenReady = true
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _state.value = _state.value.copy(isPlaying = isPlaying)
            }
        })
    }

    private fun loadListVideo() {
        _state.value.movie.episodes?.let {
            videoItems = it.map { servers ->
                servers.serverData.map { ep ->
                    Log.e("123",servers.serverData.toString() + ep.name.toString())
                    MediaItem.Builder().setUri(ep.linkM3u8).setTag(ep.name).setMediaId(ep.name!!)
                        .setMimeType(MimeTypes.APPLICATION_M3U8).build()
                }
            }.toMutableStateList()
            if (_state.value.resume.resume.isNotEmpty()) {
                val svEp = _state.value.resume.resume.split(":")
                _state.value = _state.value.copy(
                    serverSelected = svEp[0].toInt(),
                    epSelected = svEp[1]
                )
            } else {
                _state.value = _state.value.copy(
                    epSelected = videoItems[0].first().mediaId
                )
            }
            playVideo()
        }
    }

    private fun playVideo() {
        player.setMediaItem(videoItems[_state.value.serverSelected].find { it.mediaId == _state.value.epSelected }!!)
        if (_state.value.resume.resumePositionMs > 0L)
            player.seekTo(_state.value.resume.resumePositionMs)
        player.play()
    }

    fun getMovieDetail() {
        viewModelScope.launch {
            _state.update { it.copy(status = LoadStatus.Loading()) }
            movieDao.getResume(
                _state.value.slug,
                movieSourceManager.currentSource.value.index
            )?.let {
                _state.value = _state.value.copy(
                    resume = it
                )
            }
            movieDao.getFav(_state.value.slug)?.let {
                _state.value = _state.value.copy(
                    isFav = it
                )
            }
            val result =
                if (movieSourceManager.currentSource.value is MovieSourceManager.MovieSource.NguonC) {
                    apiRepository.getMovieDetailNC(_state.value.slug)
                        .onSuccess {
                            _state.update {
                                it.copy(
                                    status = LoadStatus.Success(),
                                    movie = data.toMovieDetailResponseModel(),
                                    serverSelected = 0
                                )
                            }
                        }
                } else {
                    apiRepository.getMovieDetail(_state.value.slug)
                        .onSuccess {
                            _state.update {
                                it.copy(
                                    status = LoadStatus.Success(),
                                    movie = data,
                                    serverSelected = 0
                                )
                            }
                        }
                }
            result.onError {
                setToast(this.payload.toString())
            }
                .onFailure {
                    setToast(this.message())
                }
            if (result.isSuccess){
                delay(350) // for bottom bar animation
                loadListVideo()
            }
        }
    }

    fun addFavMovie() {
        viewModelScope.launch {
            movieDao.insert(
                _state.value.movie.movie!!.toCustomMovieModel().copy(
                    isFav = !_state.value.isFav,
                    source = movieSourceManager.currentSource.value.index
                ).fixImg(movieSourceManager)
            )
            _state.value = _state.value.copy(isFav = !_state.value.isFav)
        }
    }

    fun pausePlayer() {
        player.pause()
        viewModelScope.launch {
            if (!_state.value.movie.movie?.slug.isNullOrEmpty()) {
                movieDao.insert(
                    _state.value.movie.movie!!.toCustomMovieModel().copy(
                        isResume = true,
                        source = movieSourceManager.currentSource.value.index,
                        isFav = _state.value.isFav,
                        resume = "${_state.value.serverSelected}:${_state.value.epSelected}",
                        resumePositionMs = player.currentPosition,
                        durationMs = player.duration
                    ).fixImg(movieSourceManager)
                )
            }
        }
        player.clearMediaItems()
        _state.update {
            it.copy(
                status = LoadStatus.Init(),
                movie = MovieDetailResponseModel(null, MovieDetailModel()),
                serverSelected = 0,
                isFav = false,
                resume = ResumeMovieDetail("", 0, 0),
                slug = ""
            )
        }
    }

    fun onEpChange(name: String) {
        _state.update {
            it.copy(
                epSelected = name
            )
        }
        playVideo()
    }

    fun onServerChange(int: Int) {
        _state.update {
            it.copy(
                serverSelected = int,
                epSelected = videoItems[int].first().mediaId
            )
        }
        playVideo()
    }

    fun setToast(err: String) {
        _state.update {
            it.copy(
                status = LoadStatus.Error(err)
            )
        }
    }

    fun reset() {
        _state.update {
            it.copy(
                status = LoadStatus.Init()
            )
        }
    }

    fun isFullScreen(boolean: Boolean) {
        _state.update {
            it.copy(
                isFullScreen = boolean
            )
        }
    }

    fun setSlug(slug: String) {
        _state.update {
            it.copy(
                slug = slug
            )
        }
    }

    override fun onCleared() {
        pausePlayer()
        super.onCleared()
    }
}
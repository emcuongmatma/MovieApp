package com.movieapp.ui.movie_detail



import androidx.compose.runtime.toMutableStateList
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerNotificationManager
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
@UnstableApi
@Inject constructor(
    private val apiRepository: ApiRepository,
    val player: ExoPlayer,
    private val movieSourceManager: MovieSourceManager,
    private val movieDao: MovieDao,
    val mediaSession: MediaSession,
    val notification : PlayerNotificationManager
) : ViewModel() {
    private val _state = MutableStateFlow(MovieDetailState())
    val state = _state.asStateFlow()
    private lateinit var videoItems: MutableList<List<MediaItem>>

    init {
        player.prepare()
        player.playWhenReady = true
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                this@MovieDetailViewModel._state.update { it.copy(isPlaying = isPlaying) }
            }
        })
    }

    fun getMovieDetail() {
        viewModelScope.launch {
            _state.update { it.copy(status = LoadStatus.Loading()) }
            movieDao.getResume(
                _state.value.slug,
                movieSourceManager.currentSource.value.index
            )?.let { isResume ->
                _state.update { it.copy(resume = isResume) }
            }
            movieDao.getFav(_state.value.slug)?.let { isFav ->
                _state.update { it.copy(isFav = isFav) }
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
            if (result.isSuccess) {
                delay(350) // for bottom bar animation
                loadListVideo()
            }
        }
    }

    fun setRecentlySearch() {
        _state.update { it.copy(isRecentlySearch = true) }
    }

    private fun loadListVideo() {
        _state.value.movie.episodes?.let {
            videoItems = it.map { servers ->
                servers.serverData.map { ep ->
                    MediaItem.Builder().setUri(ep.linkM3u8).setTag(ep.name).setMediaId(ep.name!!)
                        .setMediaMetadata(
                            MediaMetadata.Builder().setTitle(ep.filename)
                                .setDescription(servers.serverName).setArtworkUri(_state.value.movie.movie?.fixImg(movieSourceManager)?.posterUrl?.toUri()).build()
                        )
                        .setMimeType(MimeTypes.APPLICATION_M3U8).build()
                }
            }.toMutableStateList()
            if (_state.value.resume.resume.isNotEmpty()) {
                val svEp = _state.value.resume.resume.split(":")
                if (svEp[1].isNotEmpty()){
                    _state.update {
                        it.copy(
                            serverSelected = svEp[0].toInt(),
                            epSelected = svEp[1]
                        )
                    }
                }
            } else {
                _state.update {
                    it.copy(
                        epSelected = videoItems[0].first().mediaId
                    )
                }
            }
            playVideo(true)
        }
    }

    private fun playVideo(resume: Boolean) {
        player.playWhenReady = true
        player.setMediaItem(videoItems[_state.value.serverSelected].find { it.mediaId == _state.value.epSelected }!!)
        if (resume && _state.value.resume.resumePositionMs > 0L)
            player.seekTo(_state.value.resume.resumePositionMs)
        player.play()
    }


    fun addFavMovie() {
        viewModelScope.launch {
            movieDao.insert(
                _state.value.movie.movie!!.toCustomMovieModel().copy(
                    isFav = !_state.value.isFav,
                    isRecentlySearch = _state.value.isRecentlySearch,
                    source = movieSourceManager.currentSource.value.index
                ).fixImg(movieSourceManager)
            )
            _state.update { it.copy(isFav = !_state.value.isFav) }
        }
    }

    fun pausePlayer() {
        player.playWhenReady = false
        player.pause()
        viewModelScope.launch {
            if (!_state.value.movie.movie?.slug.isNullOrEmpty()&&_state.value.epSelected.isNotEmpty()) {
                movieDao.insert(
                    _state.value.movie.movie!!.toCustomMovieModel().copy(
                        isResume = true,
                        source = movieSourceManager.currentSource.value.index,
                        isFav = _state.value.isFav,
                        isRecentlySearch = _state.value.isRecentlySearch,
                        resume = "${_state.value.serverSelected}:${_state.value.epSelected}",
                        resumePositionMs = if (player.currentPosition - 5000 > 0) player.currentPosition - 5000 else 0,
                        durationMs = player.duration
                    ).fixImg(movieSourceManager)
                )
            }
        }
    }

    fun onEpChange(name: String) {
        _state.update {
            it.copy(
                epSelected = name
            )
        }
        playVideo(false)
    }

    fun onServerChange(int: Int) {
        _state.update {
            it.copy(
                serverSelected = int,
                epSelected = videoItems[int].first().mediaId
            )
        }
        playVideo(false)
    }

    fun setToast(err: String) {
        _state.update {
            it.copy(
                status = LoadStatus.Error(err)
            )
        }
    }

    fun reset() {
        player.clearMediaItems()
        _state.update {
            it.copy(
                movie = MovieDetailResponseModel(null, MovieDetailModel()),
                serverSelected = 0,
                isFav = false,
                resume = ResumeMovieDetail("", 0, 0),
                slug = ""
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

    @UnstableApi
    override fun onCleared() {
        notification.setPlayer(null)
        mediaSession.release()
        pausePlayer()
        player.release()
        super.onCleared()
    }
}


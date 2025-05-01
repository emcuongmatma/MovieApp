package com.movieapp.ui.movie_detail

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.movieapp.domain.model.moviedetail.MovieDetailModel
import com.movieapp.domain.model.moviedetail.MovieDetailResponseModel
import com.movieapp.domain.model.nCModel.toMovieDetailResponseModel
import com.movieapp.domain.repository.ApiRepository
import com.movieapp.ui.util.LoadStatus
import com.movieapp.ui.util.MovieSourceManager
import com.skydoves.sandwich.ApiResponse
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
    private val movieSourceManager: MovieSourceManager
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
        videoItems = _state.value.movie.episodes!!.map { servers ->
            servers.serverData.map { ep ->
                MediaItem.Builder().setUri(ep.linkM3u8).setTag(ep.name).setMediaId(ep.name!!)
                    .setMimeType(MimeTypes.APPLICATION_M3U8).build()
            }
        }.toMutableStateList()
        _state.value = _state.value.copy(
            epSelected = videoItems[0].first().mediaId
        )
        playFirstEp(0)
    }
    private fun playFirstEp(server: Int) {
        player.setMediaItem(videoItems[server][0])
        player.play()
    }
    private fun playVideo(server: Int, ep: String) {
        player.setMediaItem(videoItems[server].find { it.mediaId == ep }!!)
        player.play()
    }
    fun getMovieDetail() {
        _state.update { it.copy(status = LoadStatus.Loading()) }
        viewModelScope.launch {
             val result = if (movieSourceManager.currentSource.value is MovieSourceManager.MovieSource.NguonC) {
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
            if (result is ApiResponse.Success){
                delay(350) // for bottom bar animation
                loadListVideo()
            }
        }
    }
    fun pausePlayer() {
        player.pause()
        player.clearMediaItems()
        _state.update {
            it.copy(
                status = LoadStatus.Init(),
                movie = MovieDetailResponseModel(null, MovieDetailModel()),
                serverSelected = 0,
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
        playVideo(_state.value.serverSelected, name)
    }
    fun onServerChange(int: Int) {
        _state.update {
            it.copy(
                serverSelected = int,
                epSelected = videoItems[int].first().mediaId
            )
        }
        playFirstEp(int)
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
    fun isFullScreen(boolean: Boolean) {
        _state.update {
            it.copy(
                isFullScreen = boolean
            )
        }
    }
    fun setSlug(slug:String){
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
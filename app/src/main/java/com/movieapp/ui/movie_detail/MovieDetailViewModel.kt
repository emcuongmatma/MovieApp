package com.movieapp.ui.movie_detail

import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.movieapp.domain.repository.ApiRepository
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onFailure
import com.skydoves.sandwich.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @OptIn(UnstableApi::class)
@Inject constructor(
    private val apiRepository: ApiRepository,
    val player: ExoPlayer
) : ViewModel() {
    private val _state = MutableStateFlow(MovieDetailState())
    val state = _state.asStateFlow()
    private lateinit var videoItems:MutableList<List<MediaItem>>
    init {
        player.playWhenReady = true
        player.prepare()
    }
    private fun loadListVideo(){
        videoItems = _state.value.movie.episodes.map { servers->
            servers.serverData.map { ep->
                MediaItem.Builder().setUri(ep.linkM3u8).setTag(ep.name).setMediaId(ep.name!!).setMimeType(MimeTypes.APPLICATION_M3U8).build()
            }
        }.toMutableStateList()
        _state.value = _state.value.copy(isLoading = false, epSelected = videoItems[0].first().mediaId)
        playFirstEp(0)
    }
    private fun playFirstEp(server:Int) {
        player.replaceMediaItem(0,videoItems[server][0])
        player.play()
    }
    private fun playVideo(server:Int, ep:String) {
        player.replaceMediaItem(0, videoItems[server].find { it.mediaId == ep }!!)
        player.play()
    }
    fun getMovieDetail(slug:String) {
        viewModelScope.launch {
            apiRepository.getMovieDetail(slug)
                .onSuccess {
                    _state.update {
                        it.copy(
                            isLoading = true,
                            movie = data,
                            serverSelected = 0
                        )
                    }
                    loadListVideo()
                }
                .onError {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = this.payload.toString()
                        )
                    }
                }
                .onFailure {
                    Log.e("log", this.message())
                }
        }
    }
    fun pausePlayer(){
        player.pause()
        _state.update {
            it.copy(
                isLoading = true,
                serverSelected = 0
            )
        }
    }
    fun onEpChange(name:String) {
        _state.update {
            it.copy(
                epSelected = name
            )
        }
        playVideo(_state.value.serverSelected,name)
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
    fun isFullScreen(boolean: Boolean) {
        _state.update {
            it.copy(
                isFullScreen = boolean
            )
        }
    }
    override fun onCleared() {
        pausePlayer()
        super.onCleared()
    }
}
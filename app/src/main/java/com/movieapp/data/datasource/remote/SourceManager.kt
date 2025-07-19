package com.movieapp.data.datasource.remote

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Singleton

@Singleton
class MovieSourceManager {
    private val _currentSource = MutableStateFlow<MovieSource>(MovieSource.Ophim)
    val currentSource: StateFlow<MovieSource> = _currentSource.asStateFlow()
    fun changeSource(newSource: MovieSource) {
        _currentSource.value = newSource
    }
    sealed class MovieSource {
        abstract val index: Int
        abstract val baseURL: String
        abstract val addRecentlyURL: String
        abstract val getDetailURL: String
        abstract val getCustomHead: String
        abstract val searchHead: String
        abstract val searchTail: String
        abstract val imageURL:String
        abstract val itemPerPage:Int
        data object KKPhim : MovieSource() {
            override val index = 0
            override val baseURL = "https://phimapi.com/"
            override val addRecentlyURL = "danh-sach/phim-moi-cap-nhat-v3"
            override val getDetailURL = "phim/"
            override val getCustomHead = "v1/api/danh-sach/"
            override val searchHead = "/v1/api/tim-kiem?keyword="
            override val searchTail = "&sort_field=year"
            override val imageURL = "https://phimimg.com/"
            override val itemPerPage = 10
        }
        data object Ophim : MovieSource() {
            override val index = 1
            override val baseURL = "https://ophim1.com/"
            override val addRecentlyURL = "danh-sach/phim-moi-cap-nhat"
            override val getDetailURL = "phim/"
            override val getCustomHead = "v1/api/danh-sach/"
            override val searchHead = "/v1/api/tim-kiem?keyword="
            override val searchTail = "&sort_field=year"
            override val imageURL = "https://img.ophim.live/uploads/movies/"
            override val itemPerPage = 24
        }
        data object NguonC : MovieSource() {
            override val index = 2
            override val baseURL = "https://phim.nguonc.com/api/"
            override val addRecentlyURL = "films/danh-sach/phim-dang-chieu"
            override val getDetailURL = "film/"
            override val getCustomHead = "films/danh-sach/"
            override val searchHead = "films/search?keyword="
            override val searchTail = "&sort_field=year"
            override val imageURL = ""
            override val itemPerPage = 10
        }
    }
}
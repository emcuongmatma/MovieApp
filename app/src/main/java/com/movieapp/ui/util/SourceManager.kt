package com.movieapp.ui.util

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
        abstract val BASE_URL: String
        abstract val ADD_RECENTLY_URL: String
        abstract val GET_DETAIL_URL: String
        abstract val GET_CUSTOM_HEAD: String
        abstract val GET_CUSTOM_TAIL: String
        abstract val SEARCH_HEAD: String
        abstract val SEARCH_TAIL: String
        abstract val IMAGE_BASE_URL:String
        data object KKPhim : MovieSource() {
            override val BASE_URL = "https://phimapi.com/"
            override val ADD_RECENTLY_URL = "danh-sach/phim-moi-cap-nhat?page=1"
            override val GET_DETAIL_URL = "phim/"
            override val GET_CUSTOM_HEAD = "v1/api/danh-sach/"
            override val GET_CUSTOM_TAIL = "?page=1&sort_field=_id&sort_type=asc&year=2025&limit=10"
            override val SEARCH_HEAD = "/v1/api/tim-kiem?keyword="
            override val SEARCH_TAIL = "&sort_field=year"
            override val IMAGE_BASE_URL = "https://phimimg.com/"
        }
        data object NguonC : MovieSource() {
            override val BASE_URL = "https://phim.nguonc.com/api/"
            override val ADD_RECENTLY_URL = "films/phim-moi-cap-nhat?page=&limit=10"
            override val GET_DETAIL_URL = "film/"
            override val GET_CUSTOM_HEAD = "films/danh-sach/"
            override val GET_CUSTOM_TAIL = "?page=1&sort_field=_id&sort_type=asc&year=2025&limit=10"
            override val SEARCH_HEAD = "films/search?keyword="
            override val SEARCH_TAIL = "&sort_field=year"
            override val IMAGE_BASE_URL = ""
        }
        data object Ophim : MovieSource() {
            override val BASE_URL = "https://ophim1.com/"
            override val ADD_RECENTLY_URL = "danh-sach/phim-moi-cap-nhat?page=1"
            override val GET_DETAIL_URL = "phim/"
            override val GET_CUSTOM_HEAD = "v1/api/danh-sach/"
            override val GET_CUSTOM_TAIL = "?page=1&sort_field=_id&sort_type=asc&year=2025&limit=10"
            override val SEARCH_HEAD = "/v1/api/tim-kiem?keyword="
            override val SEARCH_TAIL = "&sort_field=year"
            override val IMAGE_BASE_URL = "https://img.ophim.live/uploads/movies/"
        }
    }
}
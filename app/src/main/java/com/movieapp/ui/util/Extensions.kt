package com.movieapp.ui.util

import com.movieapp.domain.model.custom.CustomMovieModel
import com.movieapp.domain.model.custom.CustomMovieResponseModel
import kotlin.collections.all
import kotlin.collections.contains
import kotlin.collections.filter

fun CustomMovieResponseModel.toListMovie(movieSourceManager: MovieSourceManager):List<CustomMovieModel>{
    val list: List<CustomMovieModel> = when(movieSourceManager.currentSource.value){
        is MovieSourceManager.MovieSource.KKPhim ->{
            this.data!!.items!!.filter().map { item ->
                item.copy(posterUrl = if (!item.posterUrl!!.contains("https")) movieSourceManager.currentSource.value.IMAGE_BASE_URL + item.posterUrl else item.posterUrl)
            }
        }
        is MovieSourceManager.MovieSource.Ophim ->{
            this.data!!.items!!.filter().map { item ->
                item.copy(posterUrl = if (!item.posterUrl!!.contains("https")) movieSourceManager.currentSource.value.IMAGE_BASE_URL + item.thumbUrl else item.thumbUrl)
            }
        }
        is MovieSourceManager.MovieSource.NguonC ->{
            this.items!!.filter().map { item ->
                item.copy(posterUrl = if (!item.posterUrl!!.contains("https")) movieSourceManager.currentSource.value.IMAGE_BASE_URL + item.thumbUrl else item.thumbUrl)
            }
        }
    }
    return list
}
fun List<CustomMovieModel>.converter(movieSourceManager: MovieSourceManager): List<CustomMovieModel> {
    val list: List<CustomMovieModel> = when (movieSourceManager.currentSource.value) {
        is MovieSourceManager.MovieSource.KKPhim -> {
            this.filter().map { item ->
                item.copy(posterUrl = if (!item.posterUrl!!.contains("https")) movieSourceManager.currentSource.value.IMAGE_BASE_URL + item.posterUrl else item.posterUrl)
            }
        }

        is MovieSourceManager.MovieSource.Ophim -> {
            this.filter().map { item ->
                item.copy(posterUrl = if (!item.posterUrl!!.contains("https")) movieSourceManager.currentSource.value.IMAGE_BASE_URL + item.thumbUrl else item.thumbUrl)
            }
        }

        is MovieSourceManager.MovieSource.NguonC -> {
            this.filter().map { item ->
                item.copy(posterUrl = if (!item.posterUrl!!.contains("https")) movieSourceManager.currentSource.value.IMAGE_BASE_URL + item.thumbUrl else item.thumbUrl)
            }
        }
    }
    return list
}


fun List<CustomMovieModel>.filter(): List<CustomMovieModel> {
    val allowCategory = listOf(
        "hanh-dong",
        "tre-em",
        "lich-su",
        "co-trang",
        "chien-tranh",
        "vien-tuong",
        "kinh-di",
        "tai-lieu",
        "bi-an",
        "tinh-cam",
        "tam-ly",
        "the-thao",
        "phieu-luu",
        "am-nhac",
        "gia-dinh",
        "hoc-duong",
        "hai-huoc",
        "hinh-su",
        "vo-thuat",
        "khoa-hoc",
        "than-thoai",
        "chinh-kich",
        "kinh-dien"
    )
    val list: List<CustomMovieModel> = this.filter { movie ->
        movie.category!!.all { it.slug in allowCategory }
    }
    return list
}
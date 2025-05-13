package com.movieapp.ui.util

import com.movieapp.data.datasource.remote.MovieSourceManager
import com.movieapp.data.model.custom.CustomMovieModel
import com.movieapp.data.model.custom.CustomMovieResponseModel
import kotlin.collections.all
import kotlin.collections.contains
import kotlin.text.contains

fun CustomMovieResponseModel.toListMovie(movieSourceManager: MovieSourceManager): List<CustomMovieModel> {
    val list: List<CustomMovieModel> = when (movieSourceManager.currentSource.value) {
        is MovieSourceManager.MovieSource.KKPhim -> {
            this.data!!.items!!.filter().map { item ->
                item.fixImg(movieSourceManager)
            }
        }

        is MovieSourceManager.MovieSource.Ophim -> {
            this.data!!.items!!.filter().map { item ->
                item.fixImg(movieSourceManager)
            }
        }

        is MovieSourceManager.MovieSource.NguonC -> {
            this.items!!.filter().map { item ->
                item.fixImg(movieSourceManager)
            }
        }
    }
    return list
}

fun List<CustomMovieModel>.converter(movieSourceManager: MovieSourceManager): List<CustomMovieModel> {
    val list: List<CustomMovieModel> =
        this.filter().map { item ->
            item.fixImg(movieSourceManager)
        }
    return list
}

fun CustomMovieModel.fixImg(movieSourceManager: MovieSourceManager): CustomMovieModel {
    val movie = when (movieSourceManager.currentSource.value) {
        is MovieSourceManager.MovieSource.KKPhim -> {
            this.copy(posterUrl = if (!this.posterUrl!!.contains("https")) movieSourceManager.currentSource.value.IMAGE_BASE_URL + this.posterUrl else this.posterUrl)
        }

        is MovieSourceManager.MovieSource.Ophim -> {
            this.copy(posterUrl = if (!this.posterUrl!!.contains("https")) movieSourceManager.currentSource.value.IMAGE_BASE_URL + this.thumbUrl else this.thumbUrl)
        }

        is MovieSourceManager.MovieSource.NguonC -> {
            this.copy(posterUrl = if (!this.posterUrl!!.contains("https")) movieSourceManager.currentSource.value.IMAGE_BASE_URL + this.thumbUrl else this.thumbUrl)
        }
    }
    return movie
}
fun String.convertContent():String{
    return if(this.contains("<p>")) this.removePrefix("<p>").split("</p>")[0] else this
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
package com.movieapp.domain.model.custom


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CustomMovieModel(
    @SerialName("casts")
    val casts: String? = "",
    @SerialName("episode_current")
    val episodeCurrent: String? = "",
    @SerialName("name")
    val name: String? = "",
    @SerialName("origin_name")
    val originName: String? = "",
    @SerialName("poster_url")
    val posterUrl: String? = "",
    @SerialName("quality")
    val quality: String? = "",
    @SerialName("slug")
    val slug: String? = "",
    @SerialName("thumb_url")
    val thumbUrl: String? = "",
    @SerialName("time")
    val time: String? = "",
    @SerialName("type")
    val type: String? = "",
    @SerialName("year")
    val year: Int? = 0,
    @SerialName("category")
    val category: List<CategoryModel>? = listOf()
)
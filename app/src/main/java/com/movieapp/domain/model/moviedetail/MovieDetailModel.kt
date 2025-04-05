package com.movieapp.domain.model.moviedetail


import com.movieapp.domain.model.custom.CategoryModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieDetailModel(
    @SerialName("actor")
    val actor: List<String> = listOf(),
    @SerialName("director")
    val director:List<String> = listOf(),
    //nguonc
    @SerialName("casts")
    val casts: String? = "",
    //nguonc
    @SerialName("content")
    val content: String? = "",
    @SerialName("episode_current")
    val episodeCurrent: String? = "",
    @SerialName("name")
    val name: String? = "null",
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
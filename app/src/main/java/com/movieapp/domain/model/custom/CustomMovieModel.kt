package com.movieapp.domain.model.custom


import com.movieapp.domain.model.recentlyupdate.ModifiedModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CustomMovieModel(
    @SerialName("episode_current")
    val episodeCurrent: String? = "",
    @SerialName("_id")
    val id: String? = "",
    @SerialName("modified")
    val modified: ModifiedModel? = ModifiedModel(),
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
    val year: Int? = 0
)
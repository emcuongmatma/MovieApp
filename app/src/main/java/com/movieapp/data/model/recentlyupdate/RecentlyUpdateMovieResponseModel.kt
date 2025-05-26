package com.movieapp.data.model.recentlyupdate


import com.movieapp.data.model.custom.CustomMovieModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecentlyUpdateMovieResponseModel(
    @SerialName("items")
    val items: List<CustomMovieModel>? = listOf(),
    @SerialName("pathImage")
    val pathImage: String? = ""
)
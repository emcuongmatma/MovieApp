package com.movieapp.domain.model.recentlyupdate


import com.movieapp.domain.model.custom.CustomMovieModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecentlyUpdateMovieResponseModel(
    @SerialName("items")
    val items: List<CustomMovieModel>? = listOf(),
    @SerialName("status")
    val status: Boolean? = false,
    @SerialName("pathImage")
    val pathImage: String? = ""
)
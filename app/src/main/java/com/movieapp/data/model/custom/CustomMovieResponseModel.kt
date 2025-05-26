package com.movieapp.data.model.custom


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CustomMovieResponseModel(
    @SerialName("data")
    val `data`: DataModel? = DataModel(),
    @SerialName("items")
    val items: List<CustomMovieModel>? = listOf()
)
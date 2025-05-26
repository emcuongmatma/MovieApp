package com.movieapp.data.model.custom


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DataModel(
    @SerialName("items")
    val items: List<CustomMovieModel>? = listOf()
)
package com.movieapp.domain.model.custom


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DataModel(
    @SerialName("items")
    val items: List<CustomMovieModel>? = listOf()
)
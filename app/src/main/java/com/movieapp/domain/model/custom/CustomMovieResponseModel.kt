package com.movieapp.domain.model.custom


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CustomMovieResponseModel(
    @SerialName("data")
    val `data`: DataModel? = DataModel(),
    @SerialName("msg")
    val msg: String? = "",
    @SerialName("status")
    val status: String? = ""
)
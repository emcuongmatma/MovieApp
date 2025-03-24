package com.movieapp.domain.model.moviedetail


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreatedModel(
    @SerialName("time")
    val time: String? = ""
)
package com.movieapp.domain.model.recentlyupdate


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ModifiedModel(
    @SerialName("time")
    val time: String? = ""
)
package com.movieapp.data.model.moviedetail


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServerDataModel(
    @SerialName("link_m3u8")
    val linkM3u8: String? = "",
    @SerialName("name")
    val name: String? = "",
)
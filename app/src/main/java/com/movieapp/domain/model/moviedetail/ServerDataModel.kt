package com.movieapp.domain.model.moviedetail


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServerDataModel(
    @SerialName("filename")
    val filename: String? = "",
    @SerialName("link_embed")
    val linkEmbed: String? = "",
    @SerialName("link_m3u8")
    val linkM3u8: String? = "",
    @SerialName("name")
    val name: String? = "",
    @SerialName("slug")
    val slug: String? = ""
)
package com.movieapp.domain.model.moviedetail


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EpisodeModel(
    @SerialName("server_name")
    val serverName:String?="",
    @SerialName("server_data")
    val serverData: List<ServerDataModel> = listOf()
)
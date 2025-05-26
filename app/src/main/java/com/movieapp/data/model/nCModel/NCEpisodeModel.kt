package com.movieapp.data.model.nCModel


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NCEpisodeModel(
    @SerialName("items")
    val items: List<NCItemModel>? = listOf(),
    @SerialName("server_name")
    val serverName: String? = ""
)
fun NCEpisodeModel.convertEM():com.movieapp.data.model.moviedetail.EpisodeModel{
    return com.movieapp.data.model.moviedetail.EpisodeModel(
        serverName = this.serverName,
        serverData = this.items!!.map { it.toServerDataModel() }
    )
}
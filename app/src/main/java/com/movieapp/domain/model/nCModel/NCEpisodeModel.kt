package com.movieapp.domain.model.nCModel


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NCEpisodeModel(
    @SerialName("items")
    val items: List<NCItemModel>? = listOf(),
    @SerialName("server_name")
    val serverName: String? = ""
)
fun NCEpisodeModel.convertEM():com.movieapp.domain.model.moviedetail.EpisodeModel{
    return com.movieapp.domain.model.moviedetail.EpisodeModel(
        serverName = this.serverName,
        serverData = this.items!!.map { it.toServerDataModel() }
    )
}
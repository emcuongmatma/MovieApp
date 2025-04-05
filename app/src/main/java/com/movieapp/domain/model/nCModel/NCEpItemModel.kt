package com.movieapp.domain.model.nCModel


import com.movieapp.domain.model.moviedetail.ServerDataModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NCItemModel(
    @SerialName("embed")
    val embed: String? = "",
    @SerialName("m3u8")
    val m3u8: String? = "",
    @SerialName("name")
    val name: String? = "",
    @SerialName("slug")
    val slug: String? = ""
)
fun NCItemModel.toServerDataModel(): ServerDataModel{
    return ServerDataModel(
        linkM3u8 = this.m3u8,
        name = this.name
    )
}
package com.movieapp.domain.model.moviedetail


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieDetailModel(
    @SerialName("actor")
    val actor: List<String> = listOf(),
    @SerialName("content")
    val content: String? = "",
    @SerialName("country")
    val country: List<CountryModel>? = listOf(),
    @SerialName("created")
    val created: CreatedModel? = CreatedModel(),
    @SerialName("episode_current")
    val episodeCurrent: String? = "",
    @SerialName("episode_total")
    val episodeTotal: String? = "",
    @SerialName("_id")
    val id: String? = "",
    @SerialName("name")
    val name: String? = "",
    @SerialName("origin_name")
    val originName: String? = "",
    @SerialName("poster_url")
    val posterUrl: String? = "",
    @SerialName("quality")
    val quality: String? = "",
    @SerialName("slug")
    val slug: String? = "",
    @SerialName("thumb_url")
    val thumbUrl: String? = "",
    @SerialName("time")
    val time: String? = "",
    @SerialName("trailer_url")
    val trailerUrl: String? = "",
    @SerialName("type")
    val type: String? = "",
    @SerialName("year")
    val year: Int? = 0
)

fun List<String>.toActorString(): String {
    var string = "Cast:"
    for (i in this.indices) {
        string = if (i == 0) {
            "$string ${this[i]}"
        } else{
            "$string,${this[i]}"
        }
    }
    return string
}
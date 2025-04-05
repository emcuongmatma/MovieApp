package com.movieapp.domain.model.nCModel


import com.movieapp.domain.model.moviedetail.MovieDetailModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NCMovieModel(
    @SerialName("casts")
    val casts: String? = "",
    @SerialName("category")
    val category: NCCategoryModel? = NCCategoryModel(),
    @SerialName("current_episode")
    val currentEpisode: String? = "",
    @SerialName("description")
    val description: String? = "",
    @SerialName("director")
    val director: String? = "",
    @SerialName("episodes")
    val episodes: List<NCEpisodeModel>? = listOf(),
    @SerialName("name")
    val name: String? = "",
    @SerialName("original_name")
    val originalName: String? = "",
    @SerialName("poster_url")
    val posterUrl: String? = "",
    @SerialName("quality")
    val quality: String? = "",
    @SerialName("slug")
    val slug: String? = "",
    @SerialName("thumb_url")
    val thumbUrl: String? = "",
    @SerialName("time")
    val time: String? = ""
)
fun NCMovieModel.toMovieDetailModel(): MovieDetailModel{
    return MovieDetailModel(
        director = director!!.split(", "),
        casts = casts,
        content = description,
        name = this.name,
        year = this.category?.x3?.list?.first()?.name?.toInt(),
        quality = this.quality,
        time = this.time,
        episodeCurrent = this.currentEpisode
    )
}
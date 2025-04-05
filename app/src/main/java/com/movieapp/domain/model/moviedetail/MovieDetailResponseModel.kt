package com.movieapp.domain.model.moviedetail


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieDetailResponseModel(
    @SerialName("episodes")
    val episodes: List<EpisodeModel>? = listOf(),
    @SerialName("movie")
    val movie: MovieDetailModel? = MovieDetailModel()
)
package com.movieapp.domain.model.nCModel


import com.movieapp.domain.model.moviedetail.MovieDetailResponseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NCMovieDetailResponeModel(
    @SerialName("movie")
    val movie: NCMovieModel? = NCMovieModel(),
    @SerialName("status")
    val status: String? = ""
)
fun NCMovieDetailResponeModel.toMovieDetailResponseModel(): MovieDetailResponseModel{
    return MovieDetailResponseModel(
        episodes = this.movie?.episodes?.map { it.convertEM() },
        movie = this.movie?.toMovieDetailModel()
    )
}
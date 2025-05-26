package com.movieapp.data.model.custom


import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
@Entity(tableName = "movies")
data class CustomMovieModel(
    @SerialName("casts")
    val casts: String? = "",
    @SerialName("episode_current")
    val episodeCurrent: String? = "",
    @SerialName("name")
    val name: String? = "",
    @SerialName("origin_name")
    val originName: String? = "",
    @SerialName("poster_url")
    val posterUrl: String? = "",
    @SerialName("quality")
    val quality: String? = "",
    @PrimaryKey
    @SerialName("slug")
    val slug: String = "",
    @SerialName("thumb_url")
    val thumbUrl: String? = "",
    @SerialName("time")
    val time: String? = "",
    @SerialName("type")
    val type: String? = "",
    @SerialName("year")
    val year: Int? = 0,
    @SerialName("category")
    val category: List<CategoryModel>? = listOf(),
    val isFav: Boolean? = false,
    val isResume: Boolean? = false,
    val resume:String="",
    val resumePositionMs:Long = 0L,
    val durationMs:Long = 0L,
    val source:Int?=0
)
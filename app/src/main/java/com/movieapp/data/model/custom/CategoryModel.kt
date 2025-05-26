package com.movieapp.data.model.custom


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryModel(
    @SerialName("name")
    val name: String? = "",
    @SerialName("slug")
    val slug: String? = ""
)
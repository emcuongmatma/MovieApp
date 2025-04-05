package com.movieapp.domain.model.nCModel


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NCCategoryItemModel(
    @SerialName("id")
    val id: String? = "",
    @SerialName("name")
    val name: String? = ""
)
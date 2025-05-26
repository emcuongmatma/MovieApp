package com.movieapp.data.model.nCModel


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NCCategorySubModel(
    @SerialName("group")
    val group: NCCategoryItemModel? = NCCategoryItemModel(),
    @SerialName("list")
    val list: List<NCCategoryItemModel>? = listOf()
)
package com.movieapp.domain.model.nCModel


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NCCategoryModel(
    @SerialName("2")
    val x2: NCCategorySubModel? = NCCategorySubModel(),
    @SerialName("3")
    val x3: NCCategorySubModel? = NCCategorySubModel()
)
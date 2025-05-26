package com.movieapp.data.datasource.local.typeconverter

import androidx.room.TypeConverter
import com.movieapp.data.model.custom.CategoryModel
import kotlinx.serialization.json.Json

class MovieConverter {
    private val json : Json by lazy {
        Json { ignoreUnknownKeys = true }
    }
    @TypeConverter
    fun fromCategoryList(value: List<CategoryModel>): String {
        return json.encodeToString(value)
    }
    @TypeConverter
    fun toCategoryList(value: String): List<CategoryModel> {
        return json.decodeFromString(value)
    }
}

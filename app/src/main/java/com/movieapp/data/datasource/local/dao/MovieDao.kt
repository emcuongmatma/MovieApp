package com.movieapp.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.movieapp.data.model.custom.CustomMovieModel
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movieDetail: CustomMovieModel)

    @Query("Select * From movies Where isFav = 1")
    fun getAllFavMovie(): List<CustomMovieModel>

    @Query("Select * From movies Where isResume = 1")
    fun getAllResMovie():List<CustomMovieModel>

    @Query("Select * From movies Where isRecentlySearch = 1")
    fun getRecentlySearch():Flow<List<CustomMovieModel>>

    @Query("Select resume,resumePositionMs,durationMs From movies Where slug = :slug AND source = :source ")
    suspend fun getResume(slug:String,source:Int): ResumeMovieDetail?

    @Query("Select isFav From movies Where slug = :slug")
    suspend fun getFav(slug:String): Boolean?

    @Query("Select isRecentlySearch From movies Where slug = :slug")
    suspend fun getRecentlySearch(slug:String): Boolean?

    @Delete
    fun deleteMovie(vararg users: CustomMovieModel)

    @Query("Delete From movies Where isResume = 1 AND isFav = 0")
    suspend fun delRnF()

    @Query("UPDATE  movies SET isResume = 0 Where isResume = 1 AND isFav = 1")
    suspend fun updateRaF()

    @Query("Delete From movies Where isFav = 1 AND isResume = 0")
    suspend fun delFnR()

    @Query("UPDATE movies SET isFav = 0 Where isResume = 1 AND isFav = 1")
    suspend fun updateFaR()


}
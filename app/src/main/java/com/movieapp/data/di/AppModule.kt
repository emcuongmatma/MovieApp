package com.movieapp.data.di

import android.app.Application
import androidx.media3.exoplayer.ExoPlayer
import com.movieapp.data.reppository.ApiRepositoryImpl
import com.movieapp.domain.repository.ApiRepository
import com.movieapp.ui.util.MovieSourceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideHttpClient(
        movieSourceManager: MovieSourceManager
    ): HttpClient =
        HttpClient(OkHttp.create()) {
            defaultRequest {
                url(movieSourceManager.currentSource.value.BASE_URL)
                header(HttpHeaders.ContentType, "application/json")
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
        }
    @Provides
    @Singleton
    fun provideApiRepository(httpClient: HttpClient,movieSourceManager: MovieSourceManager):ApiRepository = ApiRepositoryImpl(httpClient,
        movieSourceManager)
    @Provides
    @Singleton
    fun provideSourceManager(): MovieSourceManager = MovieSourceManager()
}
@Module
@InstallIn(ViewModelComponent::class)
object VideoPlayerModule{
    @ViewModelScoped
    @Provides
    fun provideExoPlayer(app: Application): ExoPlayer {
        return  ExoPlayer.Builder(app).build()
    }
}
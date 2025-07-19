package com.movieapp.di

import com.movieapp.data.datasource.remote.MovieSourceManager
import com.movieapp.data.repository.remote.ApiRepository
import com.movieapp.data.repository.remote.ApiRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
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
            install(HttpTimeout){
                requestTimeoutMillis = 15_000
                connectTimeoutMillis = 10_000
                socketTimeoutMillis = 10_000
            }
            defaultRequest {
                url(movieSourceManager.currentSource.value.baseURL)
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
}

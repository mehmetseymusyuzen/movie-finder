package com.example.moviefinder.data.repository

import com.example.moviefinder.BuildConfig
import com.example.moviefinder.data.api.MovieApi
import com.example.moviefinder.data.models.MovieDetail
import com.example.moviefinder.data.models.MovieResponse
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val api: MovieApi
) {
    suspend fun getPopularMovies(page: Int): Result<MovieResponse> = 
        safeApiCall { api.getPopularMovies(BuildConfig.API_KEY, page) }

    suspend fun searchMovies(query: String, page: Int): Result<MovieResponse> =
        safeApiCall { api.searchMovies(BuildConfig.API_KEY, query, page) }

    suspend fun getMovieDetails(movieId: Int): Result<MovieDetail> =
        safeApiCall { api.getMovieDetails(movieId, BuildConfig.API_KEY) }

    private suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
        return try {
            Result.success(apiCall())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 
package ru.otus.cineman.data.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.otus.cineman.data.entity.MovieModel
import ru.otus.cineman.data.entity.MoviesResult

interface MovieService {
    @GET("movie/{category}")
    fun getPopularMovies(
        @Path("category") category: String,
        @Query("page") page: Int
    ): Call<MoviesResult>

    @GET("movie/{movieId}")
    fun getMovie(
        @Path("movieId") movieId: String
    ): Call<MovieModel>
}
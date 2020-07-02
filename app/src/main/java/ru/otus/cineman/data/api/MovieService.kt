package ru.otus.cineman.data.api

import io.reactivex.Single
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
    ): Single<MoviesResult>

    @GET("movie/{movieId}")
    fun getMovie(
        @Path("movieId") movieId: String
    ): Single<MovieModel>

    @GET("search/movie")
    fun searchMoviesByText(
        @Query("query") query: String,
        @Query("page") page: Int
    ): Single<MoviesResult>
}
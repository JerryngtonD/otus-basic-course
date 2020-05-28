package ru.otus.cineman.data.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import ru.otus.cineman.data.entity.MoviesResult

interface MovieService {
    @GET("movie/popular")
    fun getPopularMovies(@Query("page") page: Int): Call<MoviesResult>
}
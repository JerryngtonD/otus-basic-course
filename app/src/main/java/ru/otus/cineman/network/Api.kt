package ru.otus.cineman.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import ru.otus.cineman.model.json.MoviesResult

interface Api {
    @GET("movie/popular")
    fun getPopularFilms(@Query("page") page: Int): Call<MoviesResult>
}
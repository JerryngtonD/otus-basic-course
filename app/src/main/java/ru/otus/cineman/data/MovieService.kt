package ru.otus.cineman.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import ru.otus.cineman.data.entity.json.MoviesResult

interface MovieService {
    @GET("movie/popular")
    fun getPopularMovies(@Query("page") page: Int): Call<MoviesResult>
}
package ru.otus.cineman

import android.app.Application
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.otus.cineman.model.MovieItem
import ru.otus.cineman.network.Api

class MovieStorage : Application() {
    companion object {
        private lateinit var movies: MutableList<MovieItem>
        fun getMovieStorage() = movies

        private lateinit var favoriteMovies: MutableList<MovieItem>
        fun getFavoriteMovieStorage() = favoriteMovies

        lateinit var api: Api
            private set

        const val BASE_URL = "https://api.themoviedb.org/3/"
        const val IMAGE_URL = "https://image.tmdb.org/t/p/w500"
        const val API_KEY = "b5cc0a88a97a9a1ff22147d617b8004f"

        var MOVIES_PAGE = 1

        var IS_INIT_LOADING = true
    }

    override fun onCreate() {
        super.onCreate()
        movies = mutableListOf()
        favoriteMovies = mutableListOf()

        initRetrofit()
    }

    private fun initRetrofit() {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(Interceptor {chain ->
                val url = chain.request()
                    .url()
                    .newBuilder()
                    .addQueryParameter("api_key", API_KEY)
                    .build()

                val request = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()
                return@Interceptor chain.proceed(request)
            })
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .let {
                api = it.create(Api::class.java)
            }

    }
}
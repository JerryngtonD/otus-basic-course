package ru.otus.cineman

import android.app.Application
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.otus.cineman.data.MovieRepository
import ru.otus.cineman.data.MovieService
import ru.otus.cineman.domain.MovieInteractor

class App : Application() {
    companion object {
        var instance: App? = null
            private set

        const val BASE_URL = "https://api.themoviedb.org/3/"
        const val IMAGE_URL = "https://image.tmdb.org/t/p/w500"
        const val API_KEY = "b5cc0a88a97a9a1ff22147d617b8004f"
    }

    var MOVIES_PAGE = 1

    var IS_INIT_LOADING = true

//    lateinit var movies: MutableList<MovieItem>
//    lateinit var favoriteMovies: MutableList<MovieItem>

    var movieRepository = MovieRepository()

    lateinit var movieService: MovieService
        private set

    lateinit var movieInteractor: MovieInteractor


    private fun initMovieInteractor() {
        movieInteractor = MovieInteractor(movieService, movieRepository)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

//        movies = mutableListOf()
//        favoriteMovies = mutableListOf()

        initRetrofit()
        initMovieInteractor()
    }

    private fun initRetrofit() {
        OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
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
            .let { okHttpClient ->
                Retrofit.Builder()
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BASE_URL)
                    .build()
                    .let {
                        movieService = it.create(MovieService::class.java)
                    }
            }
    }
}
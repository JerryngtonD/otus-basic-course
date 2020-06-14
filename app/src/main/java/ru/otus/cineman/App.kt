package ru.otus.cineman

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.otus.cineman.data.MovieStorage
import ru.otus.cineman.data.api.MovieService
import ru.otus.cineman.data.db.*
import ru.otus.cineman.domain.MovieInteractor
import ru.otus.cineman.domain.MovieRepository
import ru.otus.cineman.presentation.ApplicationParams.API_KEY
import ru.otus.cineman.presentation.ApplicationParams.BASE_URL
import ru.otus.cineman.presentation.ApplicationParams.CHANNEL
import java.util.concurrent.Executors

class App : Application() {
    companion object {
        var instance: App? = null
            private set
    }

    private var ioExecutor = Executors.newSingleThreadExecutor()

    lateinit var context: Context

    lateinit var db: MovieDb
    lateinit var movieDao: MovieDao
    lateinit var favoriteMovieDao: FavoriteMovieDao
    lateinit var watchLaterMovieDao: WatchLaterMovieDao

    lateinit var movieCache: MovieStorage
    lateinit var movieService: MovieService
    lateinit var movieRepository: MovieRepository
    lateinit var movieInteractor: MovieInteractor


    private fun initMovieInteractor() {
        movieInteractor = MovieInteractor(movieService, movieRepository)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        context = applicationContext

        initRetrofit()

        initDb()

        initMovieInteractor()

        initChannel()
    }

    private fun initDb() {
        db = Db.getInstance(context)!!
        movieDao = db.getMovieDao()
        favoriteMovieDao = db.getFavoriteMovieDao()
        watchLaterMovieDao = db.getWatchLaterMovieDao()
        movieCache = MovieStorage(ioExecutor, movieDao, favoriteMovieDao, watchLaterMovieDao)
        movieRepository = MovieRepository(movieCache)
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

    private fun initChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "FilmsSearchApp"
            val description = "Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL, name, importance)
            channel.description = description

            val notificationManager = this.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }
}
package ru.otus.cineman

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.otus.cineman.data.MovieStorage
import ru.otus.cineman.data.api.MovieService
import ru.otus.cineman.data.db.*
import ru.otus.cineman.domain.MovieInteractor
import ru.otus.cineman.domain.MovieRepository
import ru.otus.cineman.ApplicationParams.API_KEY
import ru.otus.cineman.ApplicationParams.BASE_URL
import ru.otus.cineman.ApplicationParams.CATEGORY_KEY
import ru.otus.cineman.ApplicationParams.CHANNEL
import java.util.concurrent.Executors

class App : Application() {
    companion object {
        var instance: App? = null
            private set

        var TAG = "APP"

        var moviesCategory: String = "popular"
    }

    lateinit var context: Context

    lateinit var db: MovieDb
    lateinit var movieDao: MovieDao
    lateinit var favoriteMovieDao: FavoriteMovieDao
    lateinit var watchLaterMovieDao: WatchLaterMovieDao

    lateinit var movieCache: MovieStorage
    lateinit var movieService: MovieService
    lateinit var movieRepository: MovieRepository
    lateinit var movieInteractor: MovieInteractor

    lateinit var firebaseAnalytics: FirebaseAnalytics
        private set

    lateinit var firebaseRemoteConfig: FirebaseRemoteConfig
        private set


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

        initFirebase()
    }

    private fun initDb() {
        db = Db.getInstance(context)!!
        movieDao = db.getMovieDao()
        favoriteMovieDao = db.getFavoriteMovieDao()
        watchLaterMovieDao = db.getWatchLaterMovieDao()
        movieCache = MovieStorage(movieDao, favoriteMovieDao, watchLaterMovieDao)
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
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
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

    private fun initFirebase() {
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance().apply {
            setDefaultsAsync(R.xml.remote_config)
            fetch(10)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        firebaseRemoteConfig.activate()
                    }
                    moviesCategory = firebaseRemoteConfig.getString(CATEGORY_KEY)
                }
        }

        moviesCategory = firebaseRemoteConfig.getString(CATEGORY_KEY)

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                val token = task.result?.token

                // Log and toast
                Log.d(TAG, token!!)

            })
    }
}
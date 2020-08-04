package ru.otus.cineman.integration

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.robolectric.annotation.Config
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.otus.cineman.App
import ru.otus.cineman.ApplicationParams
import ru.otus.cineman.data.MovieStorage
import ru.otus.cineman.data.api.MovieService
import ru.otus.cineman.data.db.*
import ru.otus.cineman.domain.GetMoviesCallback
import ru.otus.cineman.domain.MovieInteractor
import ru.otus.cineman.domain.MovieRepository
import ru.otus.cineman.presentation.viewmodel.MovieListViewModel


@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE)
class MovieInteractorIntegrationTest {

    lateinit var retrofit: Retrofit
    lateinit var movieService: MovieService
    lateinit var movieDb: MovieDb
    lateinit var movieDao: MovieDao
    lateinit var favoriteMovieDao: FavoriteMovieDao
    lateinit var watchLaterMovieDao: WatchLaterMovieDao
    lateinit var movieStorage: MovieStorage
    lateinit var movieRepository: MovieRepository

    lateinit var movieInteractor: MovieInteractor

    @Before
    fun setUp() {
        retrofit = OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                val url = chain.request()
                    .url()
                    .newBuilder()
                    .addQueryParameter(
                        "api_key",
                        ApplicationParams.API_KEY
                    )
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
            .let {
                Retrofit.Builder()
                    .client(it)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(ApplicationParams.BASE_URL)
                    .build()
            }

        movieService = retrofit.create(MovieService::class.java)
        movieDb = Db.getInMemoryDbInstance(InstrumentationRegistry.getInstrumentation().targetContext)
        movieDao = movieDb.getMovieDao()
        favoriteMovieDao = movieDb.getFavoriteMovieDao()
        watchLaterMovieDao = movieDb.getWatchLaterMovieDao()
        movieStorage = MovieStorage(
            movieDao,
            favoriteMovieDao,
            watchLaterMovieDao
        )
        movieRepository = MovieRepository(
            movieStorage
        )
        movieInteractor = MovieInteractor(
            movieService,
            movieRepository
        )
    }

    @Test
    fun movieInteractorSuccessCall() {
        movieService.run {
            getPopularMovies(
                "popular",
                MovieListViewModel.INIT_PAGE
            )
                .subscribeOn(Schedulers.computation())
                .subscribeBy(
                    onSuccess = { assertEquals(20, it.results.size) },
                    onError = {}
                )
        }
    }

    @Test
    fun movieInteractorErrorCall() {
        movieService.run {
            getPopularMovies(
                "unknown",
                MovieListViewModel.INIT_PAGE
            )
                .subscribeBy(
                    onSuccess = {},
                    onError = { assertEquals(404, (it as HttpException).code()) }
                )
        }
    }

    @Test
    fun movieInteractorSaveSuccessResultsToDb() {
        movieInteractor.loadInitOrRefresh(true, object : GetMoviesCallback {
            override fun onSuccess() {
                assertEquals(20, movieRepository.cachedMovies.value)
            }
            override fun onError(error: String) {}
        })
    }

    @Test
    fun movieInteractorSaveSuccessResultsToDbWithoutRefresh() {
        movieInteractor.loadInitOrRefresh(true, object : GetMoviesCallback {
            override fun onSuccess() {}
            override fun onError(error: String) {}
        })

        movieInteractor.loadInitOrRefresh(false, object : GetMoviesCallback {
            override fun onSuccess() {
                assertEquals(40, movieRepository.cachedMovies.value)
            }

            override fun onError(error: String) {}
        })
    }
}

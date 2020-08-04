package ru.otus.cineman.presentation.viewmodel

import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.otus.cineman.data.entity.FavoriteMovieModel
import ru.otus.cineman.data.entity.MovieModel
import ru.otus.cineman.data.entity.WatchLaterMovieModel
import ru.otus.cineman.domain.GetMovieFromPushCallback
import ru.otus.cineman.domain.GetMoviesCallback
import ru.otus.cineman.domain.GetSearchedMovies
import ru.otus.cineman.domain.MovieInteractor
import ru.otus.cineman.presentation.preferences.PreferencesProvider
import ru.otus.cineman.presentation.preferences.PreferencesProvider.Companion.CACHE_LOADING
import ru.otus.cineman.util.SimpleIdlingResource
import java.util.*
import javax.inject.Inject

class MovieListViewModel @Inject constructor(
    val application: Application,
    val movieInteractor: MovieInteractor
) : ViewModel() {

    @VisibleForTesting
    public val idlingResource = SimpleIdlingResource()

    companion object {
        const val INIT_PAGE = 1

        const val CACHED_DATE = "CACHED_DATE"
        const val CACHED_PAGE = "CACHED_PAGE"

        const val CACHE_ELAPSE = 20 * 60 * 1000
    }

    private var preferenceProvider = PreferencesProvider(application, CACHE_LOADING)

    var needLoading = true
    var getFromCache = false
    private var currentPage = preferenceProvider.getPreference().getInt(CACHED_PAGE, INIT_PAGE)

    private val moviesLiveData = movieInteractor.movieRepository.cachedMovies
    private val favoriteMoviesLiveData = movieInteractor.movieRepository.favoriteMovies
    private val watchLaterLiveData = movieInteractor.movieRepository.watchLaterMovies

    private val searchedMoviesLiveData = MutableLiveData<List<MovieModel>>()

    private val errorLiveData = MutableLiveData<String?>()
    private val isLoadingLiveData = MutableLiveData(false)

    private val selectedMovieLiveData = MutableLiveData<MovieModel>()

    init {
        val isNeedToRefresh = checkCacheElapsed()
        if (isNeedToRefresh) {
            onGetMovies(isNeedToRefresh)
        } else {
            getFromCache = true
        }
    }

    val movies: LiveData<List<MovieModel>>
        get() = moviesLiveData

    val favoriteMovies: LiveData<List<FavoriteMovieModel>>
        get() = favoriteMoviesLiveData

    val watchLaterMovies: LiveData<List<WatchLaterMovieModel>>
        get() = watchLaterLiveData

    val error: LiveData<String?>
        get() = errorLiveData

    val searchedMovies: LiveData<List<MovieModel>>
        get() = searchedMoviesLiveData

    val selectedMovie: LiveData<MovieModel>
        get() = selectedMovieLiveData

    val isLoading: LiveData<Boolean>
        get() = isLoadingLiveData

    fun onGetMovies(isNeedToRefresh: Boolean) {
        currentPage = INIT_PAGE
        setIsLoading(true)
        idlingResource.setIdleState(false)

        movieInteractor.loadInitOrRefresh(isNeedToRefresh, object : GetMoviesCallback {
            override fun onSuccess() {
                saveCacheDate()
                setIsLoading(false)
                idlingResource.setIdleState(true)
            }

            override fun onError(error: String) {
                setErrorLoading(error)
                setIsLoading(false)
                idlingResource.setIdleState(true)
            }
        })
    }

    fun getDataFromPush(movieId: String) {
        movieInteractor.loadMovie(movieId, object: GetMovieFromPushCallback {
            override fun onSuccess(movie: MovieModel) {
                onMovieSelect(movie)
            }
            override fun onError(error: String) {
                errorLiveData.postValue(error)
            }
        })
    }

    fun saveCacheDate() {
        preferenceProvider.getPreference().edit().apply {
            putLong(CACHED_DATE, Calendar.getInstance().timeInMillis)
        }.apply()
    }

    fun onLoadMoreMovies() {
        setIsLoading(true)
        currentPage++

        movieInteractor.loadMore(currentPage, object: GetMoviesCallback {
            override fun onSuccess() {
                saveCurrentPage()
                setIsLoading(false)
                needLoading = true
            }

            override fun onError(error: String) {
                setErrorLoading(error)
                setIsLoading(false)
                needLoading = true
            }
        })
    }


    fun onMovieSelect(movie: MovieModel) {
        selectedMovieLiveData.postValue(movie)
    }

    fun onChangeFavoriteStatus(id: Int, currentIsFavorite: Boolean) {
        val processedMovie = moviesLiveData.value!!.first { it.id == id }
        val storage = movieInteractor.movieRepository.storage
        if (currentIsFavorite) {
            storage.removeFromFavoritesById(processedMovie.id)
        } else {
            storage.addToFavorites(processedMovie)
        }
    }

    fun onDeleteFavoriteMovieById(id: Int) {
        val storage = movieInteractor.movieRepository.storage
        storage.removeFromFavoritesById(id)
    }

    fun onUpdateSelectedMovieInDetails(movie: MovieModel) {
        selectedMovieLiveData.postValue(movie)
        val storage = movieInteractor.movieRepository.storage
        storage.updateDetailsMovie(movie.id, movie.comment ?: "", movie.isLiked)
    }

    fun checkCacheElapsed(): Boolean {
        val currentDate = Calendar.getInstance().timeInMillis
        val cachedDate = preferenceProvider.getPreference().getLong(CACHED_DATE, 0L)
        val diff = currentDate - cachedDate

        return diff >= CACHE_ELAPSE
    }

    private fun saveCurrentPage() {
        preferenceProvider.getPreference().edit().apply {
            putInt(CACHED_PAGE, currentPage)
        }.apply()
    }

    fun setIsLoading(isLoading: Boolean) {
        isLoadingLiveData.postValue(isLoading)
    }

    fun setErrorLoading(error: String?) {
        errorLiveData.postValue(error)
    }

    fun removeFromWatchLater(id: Int) {
        movieInteractor.movieRepository.storage.removeFromWatchLater(id)
    }

    fun addToWatchLater(movie: MovieModel) {
        movieInteractor.movieRepository.storage.addToWatchLater(movie)
    }

    fun searchMoviesByText(query: String) {
        movieInteractor.searchMoviesByText(query, object: GetSearchedMovies {
            override fun onSuccess(movies: List<MovieModel>) {
               searchedMoviesLiveData.postValue(movies)
            }

            override fun onError(error: String) {
                errorLiveData.postValue(error)
            }
        })
    }
}
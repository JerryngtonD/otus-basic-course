package ru.otus.cineman.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.otus.cineman.App
import ru.otus.cineman.data.entity.FavoriteMovieModel
import ru.otus.cineman.data.entity.MovieModel
import ru.otus.cineman.data.mapper.MoviesMapper
import ru.otus.cineman.domain.GetMoviesCallback
import ru.otus.cineman.presentation.preferences.PreferencesProvider
import ru.otus.cineman.presentation.preferences.PreferencesProvider.Companion.CACHE_LOADING
import java.util.*

class MovieListViewModel(
    val context: Context
) : ViewModel() {
    companion object {
        const val INIT_PAGE = 1

        const val CACHED_DATE = "CACHED_DATE"
        const val CACHED_PAGE = "CACHED_PAGE"

        const val CACHE_ELAPSE = 20 * 60 * 1000
    }

    private var preferenceProvider = PreferencesProvider(context, CACHE_LOADING)
    private val movieInteractor = App.instance!!.movieInteractor

    var needLoading = true
    private var currentPage = preferenceProvider.getPreference().getInt(CACHED_PAGE, INIT_PAGE)
    private val isInitiallyViewedLiveData = MutableLiveData(false)
    private val moviesLiveData = movieInteractor.movieRepository.cachedMovies
    private val favoriteMoviesLiveData = movieInteractor.movieRepository.favoriteMovies
    private val errorLiveData = MutableLiveData<String?>()
    private val isLoadingLiveData = MutableLiveData(false)

    init {
        val isNeedToRefresh = checkCacheElapsed()
        if (isNeedToRefresh) {
            onGetMovies(isNeedToRefresh)
        }
    }

    private val selectedMovieLiveData = MutableLiveData<MovieModel>()

    val movies: LiveData<List<MovieModel>>
        get() = moviesLiveData

    val favoriteMovies: LiveData<List<FavoriteMovieModel>>
        get() = favoriteMoviesLiveData

    val error: LiveData<String?>
        get() = errorLiveData

    val selectedMovie: LiveData<MovieModel>
        get() = selectedMovieLiveData

    val isInitiallyViewed: LiveData<Boolean>
        get() = isInitiallyViewedLiveData

    val isLoading: LiveData<Boolean>
        get() = isLoadingLiveData


    fun onGetMovies(isNeedToRefresh: Boolean) {
        setIsLoading(true)
        movieInteractor.loadInitOrRefresh(isNeedToRefresh, object : GetMoviesCallback {
            override fun onSuccess() {
                saveCacheDate()
                setIsLoading(false)
            }

            override fun onError(error: String) {
                setErrorLoading(error)
                setIsLoading(false)
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
        val storage = movieInteractor.movieRepository.storage
        if (selectedMovieLiveData.value?.isSelected != null) {
            val prevSelectedMovieInLiveData = moviesLiveData.value!!.first { it.isSelected }
            storage.setMovieIsSelected(prevSelectedMovieInLiveData.id, false)
        }
        storage.setMovieIsSelected(movie.id, true)
        selectedMovieLiveData.postValue(movie)
    }

    fun onChangeFavoriteStatus(id: Int) {
        val processedMovie = moviesLiveData.value!!.first { it.movieId == id }
        val storage = movieInteractor.movieRepository.storage
        if (processedMovie.isFavorite) {
            storage.removeFromFavoritesById(processedMovie.id)
        } else {
            storage.addToFavorites(processedMovie)
        }
    }

    fun onUpdateSelectedMovieInDetails(movie: MovieModel) {
        selectedMovieLiveData.postValue(movie)
        val storage = movieInteractor.movieRepository.storage
        storage.updateDetailsMovie(movie.id, movie.comment ?: "", movie.isLiked)
    }

    private fun checkCacheElapsed(): Boolean {
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
}
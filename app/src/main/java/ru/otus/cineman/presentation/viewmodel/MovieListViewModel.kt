package ru.otus.cineman.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.otus.cineman.App
import ru.otus.cineman.data.entity.json.MovieModel
import ru.otus.cineman.domain.GetMoviesCallback

class MovieListViewModel : ViewModel() {
    companion object {
        const val INIT_PAGE = 1
    }

    private var currentPage = INIT_PAGE
    private val isInitiallyViewedLiveData = MutableLiveData(false)
    private val moviesLiveData = MutableLiveData<ArrayList<MovieModel>>()
    private val favoriteMoviesLiveData = MutableLiveData<ArrayList<MovieModel>>()
    private val isLoadingLiveData = MutableLiveData(false)
    private val errorLiveData = MutableLiveData<String>()
    private val selectedMovieLiveData = MutableLiveData<MovieModel>()

    private val movieInteractor = App.instance!!.movieInteractor

    val movies: LiveData<ArrayList<MovieModel>>
        get() = moviesLiveData

    val favoriteMovies: LiveData<ArrayList<MovieModel>>
        get() = favoriteMoviesLiveData

    val error: LiveData<String>
        get() = errorLiveData

    val selectedMovie: LiveData<MovieModel>
        get() = selectedMovieLiveData

    val isInitiallyViewed: LiveData<Boolean>
        get() = isInitiallyViewedLiveData

    val isLoading: LiveData<Boolean>
        get() = isLoadingLiveData

    fun onGetMovies() {
        setIsLoading(true)
        movieInteractor.getPopularMovies(currentPage, object : GetMoviesCallback {
            override fun onSuccess(movies: List<MovieModel>) {
                moviesLiveData.postValue(ArrayList(movies))
                setIsLoading(false)
            }

            override fun onError(error: String) {
                errorLiveData.postValue(error)
                setIsLoading(false)
            }
        })
    }

    fun onLoadMoreMovies() {
        setIsLoading(true)
        currentPage.plus(1)
        movieInteractor.getPopularMovies(currentPage, object : GetMoviesCallback {
            override fun onSuccess(movies: List<MovieModel>) {
                moviesLiveData.value!!.addAll(movies)
                moviesLiveData.postValue(moviesLiveData.value!!)
                setIsLoading(false)
            }

            override fun onError(error: String) {
                errorLiveData.postValue(error)
                setIsLoading(false)
            }
        })
    }

    fun onRefreshMovies() {
        currentPage = INIT_PAGE
        onGetMovies()
    }

    fun onMovieSelect(movie: MovieModel) {
        if (selectedMovieLiveData.value?.isSelected != null) {
            val prevSelectedMovieInLiveData = moviesLiveData.value!!.first { it.isSelected }
            val prevSelectedMoviePosition =
                moviesLiveData.value!!.indexOf(prevSelectedMovieInLiveData)
            moviesLiveData.value!!.also {
                it[prevSelectedMoviePosition].isSelected = false
            }
        }

        val selectedMovieInMoviesLiveData = moviesLiveData.value!!.first { it.id == movie.id }
        val indexSelectedMovie = moviesLiveData.value!!.indexOf(selectedMovieInMoviesLiveData)
        moviesLiveData.value!!.also {
            it[indexSelectedMovie].isSelected = true
        }

        moviesLiveData.postValue(moviesLiveData.value)
        selectedMovieLiveData.postValue(movie)
    }

    fun onChangeFavoriteStatus(id: Int) {
        val processedMovie = moviesLiveData.value!!.first { it.id == id }
        val movieIndex = moviesLiveData.value!!.indexOf(processedMovie)

        val favoriteMovies = favoriteMoviesLiveData.value ?: ArrayList()
        val listMovies = moviesLiveData.value ?: ArrayList()

        if (processedMovie.isFavorite) {
            listMovies[movieIndex].isFavorite = false
            favoriteMovies.remove(moviesLiveData.value!![movieIndex])
        } else {
            listMovies[movieIndex].isFavorite = true
            favoriteMovies.add(moviesLiveData.value!![movieIndex])
        }

        moviesLiveData.postValue(listMovies)
        favoriteMoviesLiveData.postValue(favoriteMovies)
    }

    fun onInitialViewed() {
        isInitiallyViewedLiveData.postValue(true)
    }

    fun onUpdateSelectedMovieInDetails(movie: MovieModel) {
        selectedMovieLiveData.postValue(movie)
        val selectedMovieInMoviesLiveData = moviesLiveData.value!!.first { it.id == movie.id }
        val indexSelectedMovie = moviesLiveData.value!!.indexOf(selectedMovieInMoviesLiveData)
        moviesLiveData.value!!.also {
            it[indexSelectedMovie].isLiked = movie.isLiked
            it[indexSelectedMovie].comment = movie.comment
        }.let {
            moviesLiveData.postValue(it)
        }
    }

    fun setIsLoading(isLoading: Boolean) {
        isLoadingLiveData.postValue(isLoading)
    }
}
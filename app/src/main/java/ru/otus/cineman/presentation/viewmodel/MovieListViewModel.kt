package ru.otus.cineman.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.otus.cineman.App
import ru.otus.cineman.data.entity.json.MovieModel
import ru.otus.cineman.domain.GetMoviesCallback

class MovieListViewModel: ViewModel() {
    companion object {
        const val INIT_PAGE = 1
    }

    private val isInitiallyViewedLiveData = MutableLiveData(false)
    private val moviesLiveData = MutableLiveData<List<MovieModel>>()
    private val isLoadingLiveData = MutableLiveData(false)
    private val errorLiveData = MutableLiveData<String>()
    private val selectedMovieLiveData = MutableLiveData<MovieModel>()
    private val moviePageLiveData = MutableLiveData(INIT_PAGE)


    private val movieInteractor = App.instance!!.movieInteractor

    val movies: LiveData<List<MovieModel>>
        get() = moviesLiveData

    val error: LiveData<String>
        get() = errorLiveData

    val selectedMovie: LiveData<MovieModel>
        get() = selectedMovieLiveData

    val moviePage: LiveData<Int>
        get() = moviePageLiveData

    val isInitiallyViewed: LiveData<Boolean>
        get() = isInitiallyViewedLiveData

    val isLoading: LiveData<Boolean>
        get() = isLoadingLiveData

    fun onGetMovies() {
        setIsLoading(true)
        movieInteractor.getPopularMovies(moviePageLiveData.value!!, object : GetMoviesCallback {
            override fun onSuccess(movies: List<MovieModel>) {
                moviesLiveData.postValue(movies)
                setIsLoading(false)
            }

            override fun onError(error: String) {
                errorLiveData.postValue(error)
                setIsLoading(false)
            }
        })
    }

    fun onMovieSelect(movie: MovieModel) {
        if (selectedMovieLiveData.value?.isSelected != null) {
            val prevSelectedMovieInLiveData = moviesLiveData.value!!.first { it.isSelected }
            val prevSelectedMoviePosition = moviesLiveData.value!!.indexOf(prevSelectedMovieInLiveData)
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
        moviesLiveData.value!!.onEach {
            if (it.id == id) {
                it.isFavorite = !it.isFavorite
            }
        }.let {
            moviesLiveData.postValue(it)
        }
    }

    fun onInitialViewed() {
        isInitiallyViewedLiveData.postValue(false)
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
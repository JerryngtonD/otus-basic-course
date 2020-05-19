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

    fun onGetMovies() {
        movieInteractor.getPopularMovies(moviePageLiveData.value!!, object : GetMoviesCallback {
            override fun onSuccess(movies: List<MovieModel>) {
                moviesLiveData.postValue(movies)
            }

            override fun onError(error: String) {
                errorLiveData.postValue(error)
            }
        })
    }

    fun onMovieSelect(movie: MovieModel) {
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
        isInitiallyViewedLiveData.postValue(true)
    }
}
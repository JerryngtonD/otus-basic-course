package ru.otus.cineman.domain

import androidx.lifecycle.LiveData
import ru.otus.cineman.data.MovieStorage
import ru.otus.cineman.data.entity.FavoriteMovieModel
import ru.otus.cineman.data.entity.MovieModel
import ru.otus.cineman.data.entity.WatchLaterMovieModel

class MovieRepository(
    val storage: MovieStorage
) {
    private val cachedMovieList: LiveData<List<MovieModel>> = storage.getAllMovies()
    private val favoriteMovieList: LiveData<List<FavoriteMovieModel>> = storage.getAllFavorites()
    private val watchLaterMoviesList: LiveData<List<WatchLaterMovieModel>> = storage.getAllWatchLater()


    val cachedMovies: LiveData<List<MovieModel>>
        get() = cachedMovieList

    val favoriteMovies: LiveData<List<FavoriteMovieModel>>
        get() = favoriteMovieList

    val watchLaterMovies: LiveData<List<WatchLaterMovieModel>>
        get() = watchLaterMoviesList
}


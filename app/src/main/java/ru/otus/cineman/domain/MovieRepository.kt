package ru.otus.cineman.domain

import androidx.lifecycle.LiveData
import ru.otus.cineman.data.MovieStorage
import ru.otus.cineman.data.entity.FavoriteMovieModel
import ru.otus.cineman.data.entity.MovieModel

class MovieRepository(
    val storage: MovieStorage
) {
    private val cachedMovieList: LiveData<List<MovieModel>> = storage.getAllMovies()
    private val favoriteMovieList: LiveData<List<FavoriteMovieModel>> = storage.getAllFavorite()

    val cachedMovies: LiveData<List<MovieModel>>
        get() = cachedMovieList

    val favoriteMovies: LiveData<List<FavoriteMovieModel>>
        get() = favoriteMovieList
}


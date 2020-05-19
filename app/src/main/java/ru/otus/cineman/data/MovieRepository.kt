package ru.otus.cineman.data

import ru.otus.cineman.data.entity.json.MovieModel

class MovieRepository {
    private val cachedMovies = mutableListOf<MovieModel>()

    val getCachedMovies: List<MovieModel> = cachedMovies.toList()

    fun addToCache(movies: List<MovieModel>) {
            cachedMovies.addAll(movies)
    }
}
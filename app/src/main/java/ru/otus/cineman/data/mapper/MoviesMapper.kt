package ru.otus.cineman.data.mapper

import ru.otus.cineman.data.entity.FavoriteMovieModel
import ru.otus.cineman.data.entity.MovieModel

class MoviesMapper {
    companion object {
        fun mapMovieToFavorite(movie: MovieModel): FavoriteMovieModel =
            FavoriteMovieModel(
                id = movie.id,
                image = movie.image ?: "",
                title = movie.title,
                averageRate = movie.averageRate
            )

    }
}
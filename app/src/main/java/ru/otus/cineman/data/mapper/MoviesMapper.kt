package ru.otus.cineman.data.mapper

import ru.otus.cineman.data.entity.FavoriteMovieModel
import ru.otus.cineman.data.entity.MovieModel
import ru.otus.cineman.data.entity.WatchLaterMovieModel

class MoviesMapper {
    companion object {
        fun mapMovieToFavorite(movie: MovieModel): FavoriteMovieModel =
            FavoriteMovieModel(
                id = movie.id,
                image = movie.image ?: "",
                title = movie.title,
                averageRate = movie.averageRate
            )


        fun mapWatchLaterToMovie(watchLaterMovie: WatchLaterMovieModel): MovieModel =
            MovieModel(
                movieId = watchLaterMovie.movieId,
                id = watchLaterMovie.id,
                image = watchLaterMovie.image,
                title = watchLaterMovie.title,
                description = watchLaterMovie.description,
                albumImage = watchLaterMovie.albumImage,
                averageRate = watchLaterMovie.averageRate,
                isFavorite = watchLaterMovie.isFavorite,
                isWatchLater = watchLaterMovie.isWatchLater,
                watchTime = watchLaterMovie.timeOfNotification
            )

        fun mapMovieToWatchLater(movie: MovieModel): WatchLaterMovieModel =
            WatchLaterMovieModel(
                movieId = movie.movieId,
                id = movie.id,
                image = movie.image,
                title = movie.title,
                description = movie.description,
                albumImage = movie.albumImage,
                averageRate = movie.averageRate,
                isFavorite = movie.isFavorite,
                isWatchLater = movie.isWatchLater,
                timeOfNotification = movie.watchTime
            )


    }
}
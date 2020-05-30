package ru.otus.cineman.data

import androidx.lifecycle.LiveData
import ru.otus.cineman.data.db.FavoriteMovieDao
import ru.otus.cineman.data.db.MovieDao
import ru.otus.cineman.data.entity.FavoriteMovieModel
import ru.otus.cineman.data.entity.MovieModel
import ru.otus.cineman.data.mapper.MoviesMapper
import java.util.concurrent.Executor

class MovieStorage(
    private val ioExecutor: Executor,
    val movieDao: MovieDao,
    val favoriteMovieDao: FavoriteMovieDao
) {
    fun addAllToMovies(movies: List<MovieModel>, insertFinished: () -> Unit) {
        ioExecutor.execute {
            movieDao.addAll(movies)
            insertFinished()
        }
    }

    fun refreshAllMovies(movies: List<MovieModel>, insertFinished: () -> Unit) {
        ioExecutor.execute {
            movieDao.clearAll()
            movieDao.addAll(movies)
            insertFinished()
        }
    }

    fun setMovieIsSelected(id: Int, newIsSelected: Boolean) {
        ioExecutor.execute {
            movieDao.setIsSelected(id, newIsSelected)
        }
    }

    fun getAllMovies(): LiveData<List<MovieModel>> {
        return movieDao.getAll()
    }

    fun getAllFavorite(): LiveData<List<FavoriteMovieModel>> {
        return favoriteMovieDao.getAll()
    }

    fun addToFavorites(movie: MovieModel) {
        ioExecutor.execute {
            favoriteMovieDao.add(MoviesMapper.mapMovieToFavorite(movie))
        }
    }

    fun removeFromFavoritesById(favoriteId: Int) {
        ioExecutor.execute {
            favoriteMovieDao.removeById(favoriteId)
        }
    }

    fun updateDetailsMovie(id: Int, comment: String, isLiked: Boolean) {
        ioExecutor.execute {
            movieDao.updateDetails(id, comment, isLiked)
        }
    }
}
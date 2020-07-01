package ru.otus.cineman.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import io.reactivex.Completable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import ru.otus.cineman.data.db.FavoriteMovieDao
import ru.otus.cineman.data.db.MovieDao
import ru.otus.cineman.data.db.WatchLaterMovieDao
import ru.otus.cineman.data.entity.FavoriteMovieModel
import ru.otus.cineman.data.entity.MovieModel
import ru.otus.cineman.data.entity.WatchLaterMovieModel
import ru.otus.cineman.data.mapper.MoviesMapper

class MovieStorage(
    val movieDao: MovieDao,
    val favoriteMovieDao: FavoriteMovieDao,
    val watchLaterMovieDao: WatchLaterMovieDao
) {
    fun addAllToMovies(movies: List<MovieModel>, insertFinished: () -> Unit) {
        val dispose = Completable.fromCallable {
            movieDao.addAll(movies)
        }.subscribeOn(Schedulers.computation())
            .subscribeBy(
                onComplete = { insertFinished() }
            )
    }

    fun refreshAllMovies(movies: List<MovieModel>, insertFinished: () -> Unit) {
        val dispose = Completable.fromCallable {
            movieDao.clearAll()
            movieDao.addAll(movies)
        }.subscribeOn(Schedulers.computation())
            .subscribeBy(
                onComplete = { insertFinished() }
            )
    }

    fun getAllMovies(): LiveData<List<MovieModel>> {
        return LiveDataReactiveStreams.fromPublisher(movieDao.getAll())
    }

    fun getAllFavorites(): LiveData<List<FavoriteMovieModel>> {
        return LiveDataReactiveStreams.fromPublisher(favoriteMovieDao.getAll())
    }

    fun getAllWatchLater(): LiveData<List<WatchLaterMovieModel>> {
        return LiveDataReactiveStreams.fromPublisher(watchLaterMovieDao.gerWatchLaterMovies())
    }

    fun addToFavorites(movie: MovieModel) {
        val dispose = Completable.fromRunnable {
            favoriteMovieDao.add(MoviesMapper.mapMovieToFavorite(movie))
        }.subscribeOn(Schedulers.computation())
            .subscribe()
    }

    fun removeFromFavoritesById(favoriteId: Int) {
        val dispose = Completable.fromRunnable {
            favoriteMovieDao.removeById(favoriteId)
        }.subscribeOn(Schedulers.computation())
            .subscribe()
    }

    fun updateDetailsMovie(id: Int, comment: String, isLiked: Boolean) {
        val dispose = Completable.fromRunnable {
            movieDao.updateDetails(id, comment, isLiked)
        }.subscribeOn(Schedulers.computation())
            .subscribe()
    }

    fun removeFromWatchLater(id: Int) {
        val dispose = Completable.fromRunnable {
            watchLaterMovieDao.deleteFromWatchLater(id)
        }.subscribeOn(Schedulers.computation())
            .subscribe()
    }

    fun addToWatchLater(movie: MovieModel) {
        val dispose = Completable.fromRunnable {
            watchLaterMovieDao.insertToWatchLater(MoviesMapper.mapMovieToWatchLater(movie))
        }.subscribeOn(Schedulers.computation())
            .subscribe()
    }
}
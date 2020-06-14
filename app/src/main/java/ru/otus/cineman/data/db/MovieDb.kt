package ru.otus.cineman.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.otus.cineman.data.entity.FavoriteMovieModel
import ru.otus.cineman.data.entity.MovieModel
import ru.otus.cineman.data.entity.WatchLaterMovieModel

@Database(entities = [MovieModel::class, FavoriteMovieModel::class, WatchLaterMovieModel::class], version = 1, exportSchema = false)
abstract class MovieDb : RoomDatabase() {
    abstract fun getMovieDao(): MovieDao
    abstract fun getFavoriteMovieDao(): FavoriteMovieDao
    abstract fun getWatchLaterMovieDao(): WatchLaterMovieDao
}
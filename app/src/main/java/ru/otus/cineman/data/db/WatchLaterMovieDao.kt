package ru.otus.cineman.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable
import ru.otus.cineman.data.entity.WatchLaterMovieModel

@Dao
interface WatchLaterMovieDao {
    @Query("SELECT * FROM watch_later_table")
    fun gerWatchLaterMovies(): Flowable<List<WatchLaterMovieModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertToWatchLater(watchLaterMovie: WatchLaterMovieModel)

    @Query("DELETE FROM watch_later_table WHERE id = :id")
    fun deleteFromWatchLater(id: Int)
}
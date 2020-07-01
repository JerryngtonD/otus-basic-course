package ru.otus.cineman.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable
import ru.otus.cineman.data.entity.MovieModel

@Dao
interface MovieDao {

    @Query("SELECT * FROM movie_table order by movieId ASC LIMIT :moviesCount")
    fun getLastMovies(moviesCount: Int = 20): Flowable<List<MovieModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAll(movies: List<MovieModel>)

    @Query("SELECT * FROM movie_table order by movieId")
    fun getAll(): Flowable<List<MovieModel>>

    @Query("DELETE FROM movie_table")
    fun clearAll()

    @Query("UPDATE movie_table SET comment = :comment, isLiked = :isLiked WHERE id = :id")
    fun updateDetails(id: Int, comment: String, isLiked: Boolean)
}
package ru.otus.cineman.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.otus.cineman.data.entity.MovieModel

@Dao
interface MovieDao {

    @Query("SELECT * FROM movie_table order by movieId ASC LIMIT :moviesCount")
    fun getLastMovies(moviesCount: Int = 20): LiveData<List<MovieModel>>

    @Insert
    fun addAll(movies: List<MovieModel>)

    @Query("SELECT * FROM movie_table order by movieId")
    fun getAll(): LiveData<List<MovieModel>>

    @Query("SELECT COUNT(id) FROM movie_table")
    fun getCount(): Int

    @Query("DELETE FROM movie_table")
    fun clearAll()

    @Query("UPDATE movie_table SET isSelected = :newIsSelected WHERE id = :id")
    fun setIsSelected(id: Int, newIsSelected: Boolean)

    @Query("UPDATE movie_table SET comment = :comment, isLiked = :isLiked WHERE id = :id")
    fun updateDetails(id: Int, comment: String, isLiked: Boolean)
}
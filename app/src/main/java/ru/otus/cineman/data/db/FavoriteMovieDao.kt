package ru.otus.cineman.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Flowable
import ru.otus.cineman.data.entity.FavoriteMovieModel

@Dao
interface FavoriteMovieDao {
    @Insert
    fun add(movie: FavoriteMovieModel)

    @Query("SELECT * FROM favorite_table order by movieId")
    fun getAll(): Flowable<List<FavoriteMovieModel>>

    @Query("DELETE FROM favorite_table WHERE id = :id")
    fun removeById(id: Int)
}
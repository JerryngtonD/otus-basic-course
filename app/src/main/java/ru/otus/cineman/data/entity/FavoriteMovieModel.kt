package ru.otus.cineman.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_table", indices = [Index("id", unique = true)])
data class FavoriteMovieModel(
    @PrimaryKey(autoGenerate = true)
    var movieId: Int? = null,
    var id: Int,
    var image: String,
    var title: String,
    var averageRate: String
)
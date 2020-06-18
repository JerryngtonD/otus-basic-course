package ru.otus.cineman.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "watch_later_table")
data class WatchLaterMovieModel (
    @PrimaryKey(autoGenerate = true)
    var movieId: Int,
    var id: Int,
    var image: String?,
    var title: String,
    var description: String,
    var albumImage: String?,
    var averageRate: String,
    var isFavorite: Boolean = false,
    var isWatchLater: Boolean = false,
    var timeOfNotification: Long = 0
)
package ru.otus.cineman.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "movie_table", indices = [Index("id", unique = true)])
data class MovieModel (
    @PrimaryKey(autoGenerate = true)
    var movieId: Int,
    var id: Int,
    @SerializedName("poster_path") var image: String?,
    @SerializedName("title") var title: String,
    @SerializedName("overview") var description: String,
    @SerializedName("backdrop_path") var albumImage: String?,
    @SerializedName("vote_average") var averageRate: String,
    var isSelected: Boolean = false,
    var isLiked: Boolean = false,
    var comment: String? = "",
    var isFavorite: Boolean = false
)
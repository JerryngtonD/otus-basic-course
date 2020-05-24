package ru.otus.cineman.data.entity.json

import com.google.gson.annotations.SerializedName

data class MovieModel (
    @SerializedName("poster_path") var image: String,
    @SerializedName("id") var id: Int,
    @SerializedName("title") var title: String,
    @SerializedName("overview") var description: String,
    @SerializedName("backdrop_path") var albumImage: String,
    @SerializedName("vote_average") var averageRate: String
) {
    var isSelected: Boolean = false
    var isLiked: Boolean = false
    var comment: String? = ""
    var isFavorite: Boolean = false
}
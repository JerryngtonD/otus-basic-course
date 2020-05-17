package ru.otus.cineman.model.json

import com.google.gson.annotations.SerializedName

data class MovieModel (
    @SerializedName("poster_path") var image: String,
    @SerializedName("id") var id: Int,
    @SerializedName("title") var title: String,
    @SerializedName("overview") var description: String
)
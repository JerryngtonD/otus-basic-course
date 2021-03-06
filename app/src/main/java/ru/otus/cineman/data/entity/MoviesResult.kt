package ru.otus.cineman.data.entity

import com.google.gson.annotations.SerializedName
import ru.otus.cineman.data.entity.MovieModel

data class MoviesResult(
    @SerializedName("page") var page: Int,
    @SerializedName("total_results") var total: Int,
    @SerializedName("results") var results: List<MovieModel>
)
package ru.otus.cineman.model.json

import com.google.gson.annotations.SerializedName

data class MoviesResult(
    @SerializedName("page") var page: Int,
    @SerializedName("total_results") var total: Int,
    @SerializedName("results") var results: List<MovieModel>
)
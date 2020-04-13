package ru.otus.cineman.model

data class Film(
    var id: Int,
    var titleId: Int,
    var imageId: Int,
    var descriptionId: Int,
    var isSelected: Boolean = false,
    var isLiked: Boolean = false,
    var comment: String = ""
)
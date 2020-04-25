package ru.otus.cineman.view_holder

import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.otus.cineman.R
import ru.otus.cineman.model.MovieItem

class MovieItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val movieTitle: TextView = itemView.findViewById(R.id.movie_title)
    val movieImage: ImageView = itemView.findViewById(R.id.movie_icon)
    val movieIsFavoriteIcon: Button = itemView.findViewById(R.id.isFavorite)

    fun bind(movieItem: MovieItem) {
        setTitle(movieItem)
        setImage(movieItem)
        setIsFavorite(movieItem)
    }

    fun setTitle(movieItem: MovieItem) {
        movieTitle.text = itemView.resources.getString(movieItem.titleId)
        if (movieItem.isSelected) {
            movieTitle.setTextColor(Color.GREEN)
        } else {
            movieTitle.setTextColor(Color.RED)
        }
    }

    fun setImage(movieItem: MovieItem) {
        movieImage.setImageResource(movieItem.imageId)
    }

    fun setIsFavorite(movieItem: MovieItem) {
        movieIsFavoriteIcon.visibility = if (movieItem.isFavorite) View.VISIBLE else View.INVISIBLE
    }
}
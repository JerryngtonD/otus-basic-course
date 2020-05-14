package ru.otus.cineman.view_holder

import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
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
        movieTitle.text = movieItem.title
        if (movieItem.isSelected) {
            movieTitle.setTextColor(Color.GREEN)
        } else {
            movieTitle.setTextColor(Color.RED)
        }
    }

    fun setImage(movieItem: MovieItem) {
        //TODO: change
        movieImage.setImageResource(R.drawable.incognito)
    }

    fun setIsFavorite(movieItem: MovieItem) {
        val iconByIsFavorite = if (movieItem.isFavorite) R.drawable.favorite_on else R.drawable.favorite_off
        movieIsFavoriteIcon.background = ResourcesCompat.getDrawable(itemView.resources, iconByIsFavorite, null)
    }
}
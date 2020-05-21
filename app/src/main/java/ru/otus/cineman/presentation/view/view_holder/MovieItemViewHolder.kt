package ru.otus.cineman.presentation.view.view_holder

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.otus.cineman.App.Companion.IMAGE_URL
import ru.otus.cineman.R
import ru.otus.cineman.data.entity.json.MovieModel

class MovieItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val movieTitle: TextView = itemView.findViewById(R.id.movie_title)
    val movieImage: ImageView = itemView.findViewById(R.id.movie_icon)
    val movieRate: TextView = itemView.findViewById(R.id.movie_rate)
    val movieIsFavoriteIcon: Button = itemView.findViewById(R.id.isFavorite)

    fun bind(movieItem: MovieModel) {
        setTitle(movieItem)
        setRate(movieItem)
        setImage(movieItem)
        setIsFavorite(movieItem)
    }

    fun setTitle(movie: MovieModel) {
        movieTitle.text = movie.title
        if (movie.isSelected) {
            movieTitle.setTextColor(Color.GREEN)
        } else {
            movieTitle.setTextColor(Color.RED)
        }
    }

    fun setImage(movie: MovieModel) {
        Glide.with(movieImage.context)
            .load("$IMAGE_URL${movie.image}")
            .placeholder(R.drawable.ic_loading)
            .error(R.drawable.ic_error)
            .into(movieImage)
    }

    @SuppressLint("SetTextI18n")
    fun setRate(movie: MovieModel) {
        movieRate.text = itemView.resources.getString(R.string.rate) + movie.averageRate
    }

    fun setIsFavorite(movieItem: MovieModel) {
        val iconByIsFavorite = if (movieItem.isFavorite) R.drawable.favorite_on else R.drawable.favorite_off
        movieIsFavoriteIcon.background = ResourcesCompat.getDrawable(itemView.resources, iconByIsFavorite, null)
    }
}
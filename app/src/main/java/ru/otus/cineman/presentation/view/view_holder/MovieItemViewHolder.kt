package ru.otus.cineman.presentation.view.view_holder

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.otus.cineman.R
import ru.otus.cineman.data.entity.MovieModel
import ru.otus.cineman.presentation.ApplicationParams.IMAGE_URL

class MovieItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val movieTitle: TextView = itemView.findViewById(R.id.movie_title)
    val movieImage: ImageView = itemView.findViewById(R.id.movie_icon)
    val movieIsFavoriteIcon: Button = itemView.findViewById(R.id.isFavorite)
    val movieRateNew: TextView = itemView.findViewById(R.id.rating_text)
    val ratingProgressBar: ProgressBar = itemView.findViewById(R.id.rating_progress_bar)

    fun bind(movieItem: MovieModel) {
        setTitle(movieItem)
        setRate(movieItem)
        setImage(movieItem)
        setIsFavorite(movieItem)
    }

    fun setTitle(movie: MovieModel) {
        movieTitle.text = movie.title
    }

    fun setImage(movie: MovieModel) {
        Glide.with(movieImage.context)
            .load("${IMAGE_URL}w500${movie.image}")
            .placeholder(R.drawable.ic_loading)
            .error(R.drawable.ic_error)
            .into(movieImage)
    }

    fun setRate(movie: MovieModel) {
        movieRateNew.text = movie.averageRate
        ratingProgressBar.progress = movie.averageRate.toFloat().times(10).toInt()
    }

    fun setIsFavorite(movieItem: MovieModel) {
        val iconByIsFavorite = if (movieItem.isFavorite) R.drawable.favorite_on else R.drawable.favorite_off
        movieIsFavoriteIcon.background = ResourcesCompat.getDrawable(itemView.resources, iconByIsFavorite, null)
    }
}
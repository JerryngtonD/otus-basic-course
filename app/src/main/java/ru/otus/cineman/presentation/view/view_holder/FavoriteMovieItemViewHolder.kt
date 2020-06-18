package ru.otus.cineman.presentation.view.view_holder

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.otus.cineman.R
import ru.otus.cineman.data.entity.FavoriteMovieModel
import ru.otus.cineman.presentation.ApplicationParams.IMAGE_URL

class FavoriteMovieItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val movieTitle: TextView = itemView.findViewById(R.id.movie_title)
    val movieImage: ImageView = itemView.findViewById(R.id.movie_icon)

    fun bind(movie: FavoriteMovieModel) {
        setTitle(movie)
        setImage(movie)
    }

    fun setTitle(movie: FavoriteMovieModel) {
        if (movie.title.length > 15) {
            movieTitle.setLines(3)
        }
        movieTitle.text = movie.title
        movieTitle.setTextColor(Color.RED)
    }

    fun setImage(movie: FavoriteMovieModel) {
        Glide.with(movieImage.context)
            .load("${IMAGE_URL}${movie.image}")
            .placeholder(R.drawable.ic_loading)
            .error(R.drawable.ic_error)
            .into(movieImage)
    }
}
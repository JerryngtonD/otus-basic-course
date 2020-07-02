package ru.otus.cineman.presentation.view.view_holder

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.otus.cineman.ApplicationParams
import ru.otus.cineman.R
import ru.otus.cineman.data.entity.MovieModel

class SearchMovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
    val movieTitle: TextView = itemView.findViewById(R.id.movie_title)
    val movieImage: ImageView = itemView.findViewById(R.id.movie_icon)
    val movieRateNew: TextView = itemView.findViewById(R.id.rating_text)
    val ratingProgressBar: ProgressBar = itemView.findViewById(R.id.rating_progress_bar)

    fun bind(movieItem: MovieModel) {
        setTitle(movieItem)
        setRate(movieItem)
        setImage(movieItem)
    }

    fun setTitle(movie: MovieModel) {
        movieTitle.text = movie.title
    }

    fun setImage(movie: MovieModel) {
        Glide.with(movieImage.context)
            .load("${ApplicationParams.IMAGE_URL}w500${movie.image}")
            .placeholder(R.drawable.ic_loading)
            .error(R.drawable.ic_error)
            .into(movieImage)
    }

    fun setRate(movie: MovieModel) {
        movieRateNew.text = movie.averageRate
        ratingProgressBar.progress = movie.averageRate.toFloat().times(10).toInt()
    }
}
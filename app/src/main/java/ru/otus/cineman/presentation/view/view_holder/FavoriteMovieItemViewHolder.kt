package ru.otus.cineman.presentation.view.view_holder

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.otus.cineman.R
import ru.otus.cineman.data.entity.json.MovieModel

class FavoriteMovieItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val movieTitle: TextView = itemView.findViewById(R.id.movie_title)
    val movieImage: ImageView = itemView.findViewById(R.id.movie_icon)

    fun bind(movieItem: MovieModel) {
        setTitle(movieItem)
        setImage(movieItem)
    }

    fun setTitle(movieItem: MovieModel) {
        if (movieItem.title.length > 15) {
            movieTitle.setLines(3)
        }
        movieTitle.text = movieItem.title
        movieTitle.setTextColor(Color.RED)
    }

    fun setImage(movieItem: MovieModel) {
        Glide.with(movieImage.context)
            .load(movieItem.image)
            .placeholder(R.drawable.ic_loading)
            .error(R.drawable.ic_error)
            .into(movieImage)
    }
}
package ru.otus.cineman.view_holder

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.otus.cineman.R
import ru.otus.cineman.model.MovieItem

class FavoriteMovieItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
    val movieTitle: TextView = itemView.findViewById(R.id.movie_title)
    val movieImage: ImageView = itemView.findViewById(R.id.movie_icon)

    fun bind(movieItem: MovieItem) {
        setTitle(movieItem)
        setImage(movieItem)
    }

    fun setTitle(movieItem: MovieItem) {
        movieTitle.text = movieItem.title
        movieTitle.setTextColor(Color.RED)
    }

    fun setImage(movieItem: MovieItem) {
          Glide.with(movieImage.context)
            .load(movieItem.image)
            .into(movieImage)
    }
}
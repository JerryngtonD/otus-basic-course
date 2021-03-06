package ru.otus.cineman.presentation.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.otus.cineman.R
import ru.otus.cineman.data.entity.MovieModel
import ru.otus.cineman.presentation.view.view_holder.MovieItemViewHolder

class MovieItemAdapter(
    val inflater: LayoutInflater,
    val listener: OnMovieClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var items = ArrayList<MovieModel>()

    fun setItems(models: List<MovieModel>) {
        items.clear()
        items.addAll(models)
        notifyDataSetChanged()
    }

    fun setItemsWithoutDiff(movies: List<MovieModel>) {
        items.addAll(movies)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MovieItemViewHolder(inflater.inflate(R.layout.item_movie, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MovieItemViewHolder) {
            val item = items[position]

            holder.itemView.findViewById<View>(R.id.show_more)
                .setOnClickListener {
                    listener.onDetailsClick(item)
                }
            holder.itemView.findViewById<View>(R.id.movie_icon)
                .setOnClickListener {
                    listener.onChangeFavoriteStatus(position)
            }

            holder.itemView.findViewById<View>(R.id.isFavorite)
                .setOnClickListener {
                    listener.onChangeFavoriteStatus(position)
                }

            holder.bind(item)
        }
    }

    interface OnMovieClickListener {
        fun onDetailsClick(movie: MovieModel)
        fun onChangeFavoriteStatus(position: Int)
    }
}
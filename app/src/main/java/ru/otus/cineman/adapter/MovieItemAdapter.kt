package ru.otus.cineman.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.otus.cineman.R
import ru.otus.cineman.model.MovieItem
import ru.otus.cineman.view_holder.MovieItemViewHolder

class MovieItemAdapter(
    val inflater: LayoutInflater,
    val items: MutableList<MovieItem>,
    val listener: OnMovieCLickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MovieItemViewHolder(inflater.inflate(R.layout.item_movie, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MovieItemViewHolder) {
            holder.itemView.findViewById<View>(R.id.show_more)
                .setOnClickListener {
                    listener.onMoreClick(items[position])
                }

            holder.itemView.findViewById<View>(R.id.movie_icon)
                .setOnClickListener {
                    listener.onChangeFavoriteStatus(items[position])
            }

            holder.itemView.findViewById<View>(R.id.isFavorite)
                .setOnClickListener {
                    listener.onChangeFavoriteStatus(items[position])
                }

            val movieItem = items[position]
            holder.bind(movieItem)
        }


    }

    fun add(position: Int, item: MovieItem) {
        items.add(position, item)
        notifyItemInserted(position)
    }

    interface OnMovieCLickListener {
        fun onMoreClick(movieItem: MovieItem)
        fun onChangeFavoriteStatus(movieItem: MovieItem)
    }
}
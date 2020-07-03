package ru.otus.cineman.presentation.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.otus.cineman.R
import ru.otus.cineman.data.entity.MovieModel
import ru.otus.cineman.presentation.view.view_holder.SearchMovieViewHolder

class SearchMovieAdapter(
    val inflater: LayoutInflater,
    val listener: OnSearchMovieClickListener
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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

    fun clearItems() {
        items.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SearchMovieViewHolder(inflater.inflate(R.layout.search_movie_item, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SearchMovieViewHolder) {
            val item = items[position]

            holder.itemView.findViewById<View>(R.id.show_more)
                .setOnClickListener {
                    listener.onSearchDetailsClick(item)
                }

            holder.bind(item)
        }
    }
}

interface OnSearchMovieClickListener {
    fun onSearchDetailsClick(movie: MovieModel)
}
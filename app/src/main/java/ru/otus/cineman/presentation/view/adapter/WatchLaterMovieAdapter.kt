package ru.otus.cineman.presentation.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.watch_later_item.view.*
import ru.otus.cineman.R
import ru.otus.cineman.data.entity.WatchLaterMovieModel
import ru.otus.cineman.presentation.view.view_holder.WatchLaterMovieViewHolder

class WatchLaterMovieAdapter(private val clickListener: OnItemClickListener) : RecyclerView.Adapter<WatchLaterMovieViewHolder>() {
    private val items = ArrayList<WatchLaterMovieModel>()

    fun setItems(movies: List<WatchLaterMovieModel>) {
        items.clear()
        items.addAll(movies)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchLaterMovieViewHolder {
        return WatchLaterMovieViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.watch_later_item, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: WatchLaterMovieViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)

        holder.itemView.watch_later_time_text.setOnClickListener {
            clickListener.addClick(item, position)
        }
        holder.itemView.cancel_button.setOnClickListener {
            clickListener.removeClick(item, position)
        }
    }
}

interface OnItemClickListener {
    fun addClick (watchLaterMovie: WatchLaterMovieModel, itemPosition: Int)
    fun removeClick (watchLaterMovie: WatchLaterMovieModel, itemPosition: Int)
}
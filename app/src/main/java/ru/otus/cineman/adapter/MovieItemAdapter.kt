package ru.otus.cineman.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.otus.cineman.R
import ru.otus.cineman.model.MovieItem
import ru.otus.cineman.view_holder.MovieItemViewHolder

class MovieItemAdapter(
    val inflater: LayoutInflater,
    val items: List<MovieItem>,
    val listener: OnMovieCLickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val ELAPSED_DELTA_TIME = 800
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MovieItemViewHolder(inflater.inflate(R.layout.item_movie, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    var startTime: Long = 0
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MovieItemViewHolder) {
            holder.itemView.findViewById<View>(R.id.show_more)
                .setOnClickListener {
                    listener.onMoreClick(position)
                }

            holder.itemView.setOnTouchListener {view, motionEvent ->
                val action = motionEvent.action

                if (action == MotionEvent.ACTION_DOWN) {
                    startTime = motionEvent.eventTime
                    holder.itemView.setBackgroundColor(Color.LTGRAY)
                }

                if (action == MotionEvent.ACTION_UP) {
                    val elapseTime = motionEvent.eventTime - startTime
                    holder.itemView.setBackgroundColor(Color.WHITE)
                    if (elapseTime > ELAPSED_DELTA_TIME) {
                        listener.onSaveToFavorites(position)
                        startTime = 0
                    }
                }

                true
            }

            // Old variant without backlight

//            holder.itemView.setOnLongClickListener {
//                listener.onSaveToFavorites(position)
//                true
//            }

            holder.itemView.findViewById<View>(R.id.isFavorite)
                .setOnClickListener {
                    listener.onDeleteFromFavorites(position)
                }

            val movieItem = items[position]
            holder.bind(movieItem)
        }


    }

    interface OnMovieCLickListener {
        fun onMoreClick(position: Int)
        fun onSaveToFavorites(position: Int)
        fun onDeleteFromFavorites(position: Int)
    }
}
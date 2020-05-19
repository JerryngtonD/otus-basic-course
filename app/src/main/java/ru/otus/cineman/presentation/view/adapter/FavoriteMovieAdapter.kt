//package ru.otus.cineman.presentation.view.adapter
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import ru.otus.cineman.R
//import ru.otus.cineman.data.entity.MovieItem
//import ru.otus.cineman.presentation.view.view_holder.FavoriteMovieItemViewHolder
//
//class FavoriteMovieAdapter(
//    val inflater: LayoutInflater,
//    val items: List<MovieItem>
//) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        return FavoriteMovieItemViewHolder(inflater.inflate(R.layout.item_favorite_movie, parent, false))
//    }
//
//    override fun getItemCount(): Int {
//        return items.size
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        if (holder is FavoriteMovieItemViewHolder) {
//            val movieItem = items[position]
//            holder.bind(movieItem)
//        }
//    }
//}
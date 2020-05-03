package ru.otus.cineman.fragment

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat.getDrawable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import ru.otus.cineman.MovieStorage
import ru.otus.cineman.R
import ru.otus.cineman.activity.MainActivity.Companion.IS_PREVIEW_MOVIES_UPDATED_BY_DETAILS
import ru.otus.cineman.activity.MainActivity.Companion.UPDATED_COMMENT
import ru.otus.cineman.activity.MainActivity.Companion.UPDATED_IS_LIKED_STATUS
import ru.otus.cineman.adapter.MovieItemAdapter
import ru.otus.cineman.animation.CustomItemAnimator
import ru.otus.cineman.model.MovieItem

class MoviesListFragment : Fragment() {
    var listener: MovieListListener? = null
    var recycler: RecyclerView? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        listener = activity as MovieListListener
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movies_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemDecoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(getDrawable(resources, R.drawable.divider, null)!!)
        recycler = view.findViewById<RecyclerView>(R.id.recyclerView).apply {
            adapter = createAdapter(view)
            itemAnimator = CustomItemAnimator()
            addItemDecoration(itemDecoration)
        }

        val isSelectedMovieUpdated =
            arguments?.getBoolean(IS_PREVIEW_MOVIES_UPDATED_BY_DETAILS) ?: false
        if (isSelectedMovieUpdated) {
            val comment = arguments?.getString(UPDATED_COMMENT)
            val isLikedStatus = arguments?.getBoolean(UPDATED_IS_LIKED_STATUS)
            updateSelectedMovieDetailsInfo(comment, isLikedStatus)
        }
    }

    private fun createAdapter(view: View): MovieItemAdapter {
        return MovieItemAdapter(
            LayoutInflater.from(activity),
            MovieStorage.getMovieStorage(),
            object : MovieItemAdapter.OnMovieCLickListener {

                override fun onMoreClick(movieItem: MovieItem) {
                    val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
                    val adapter = recyclerView.adapter as MovieItemAdapter
                    unselectAllItems()
                    movieItem.isSelected = true
                    adapter.notifyDataSetChanged()
                    listener?.onMoreClick(movieItem)
                }

                override fun onChangeFavoriteStatus(movieItem: MovieItem) {
                    val movieItemAdapter = recycler?.adapter as MovieItemAdapter
                    val updatedMoviePosition = movieItemAdapter.items.indexOf(movieItem)
                    movieItemAdapter.items[updatedMoviePosition].apply {
                        isFavorite = !isFavorite
                    }
                    movieItemAdapter.notifyItemChanged(updatedMoviePosition)
                }

                private fun unselectAllItems() {
                    val movieItemAdapter = recycler?.adapter as MovieItemAdapter
                    movieItemAdapter.items.forEach {
                        it.isSelected = false
                    }
                }
            })
    }

    private fun updateSelectedMovieDetailsInfo(comment: String?, isLikedStatus: Boolean?) {
        val movieItemAdapter = recycler?.adapter as MovieItemAdapter
        val selectedMovie = movieItemAdapter.items.first { it.isSelected }

        val isUpdatedMovieDetails =
            !(selectedMovie.comment == comment && selectedMovie.isLiked == isLikedStatus)

        if (isUpdatedMovieDetails) {
            val positionSelectedMovie = movieItemAdapter.items.indexOf(selectedMovie)
            selectedMovie.comment = comment
            selectedMovie.isLiked = isLikedStatus ?: false
            movieItemAdapter.notifyItemChanged(positionSelectedMovie)
        }
    }
}

interface MovieListListener {
    fun onMoreClick(movieItem: MovieItem)
}
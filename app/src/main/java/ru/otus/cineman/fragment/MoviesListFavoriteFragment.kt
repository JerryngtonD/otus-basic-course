package ru.otus.cineman.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import ru.otus.cineman.MovieStorage.Companion.getFavoriteMovieStorage
import ru.otus.cineman.R
import ru.otus.cineman.adapter.FavoriteMovieAdapter
import ru.otus.cineman.animation.CustomItemAnimator

class MoviesListFavoriteFragment : Fragment() {
    var recycler: RecyclerView? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite_movies, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val itemDecoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.divider,
                null
            )!!)
        recycler = view.findViewById<RecyclerView>(R.id.favoriteRecyclerView).apply {
            adapter = createAdapter(view)
            itemAnimator = CustomItemAnimator()
            addItemDecoration(itemDecoration)
        }
    }

    private fun createAdapter(view: View): FavoriteMovieAdapter {
        return FavoriteMovieAdapter(
            LayoutInflater.from(activity),
            getFavoriteMovieStorage()
        )
    }

}
package ru.otus.cineman.presentation.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import dagger.android.support.DaggerFragment
import ru.otus.cineman.R
import ru.otus.cineman.di.modules.ViewModelFactory
import ru.otus.cineman.presentation.view.activity.OnCloseFragmentListener
import ru.otus.cineman.presentation.view.adapter.FavoriteMovieAdapter
import ru.otus.cineman.presentation.view.animation.CustomItemAnimator
import ru.otus.cineman.presentation.viewmodel.MovieListViewModel
import javax.inject.Inject


class MoviesListFavoriteFragment : DaggerFragment() {
    companion object {
        const val TAG = "MOVIES_LIST_FAVORITE"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory


    lateinit var listener: OnCloseFragmentListener
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: FavoriteMovieAdapter
    private lateinit var viewModel: MovieListViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        if (activity is OnCloseFragmentListener) {
            listener = activity as OnCloseFragmentListener
        }
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite_movies, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecycler()
        initSwipeDeleteListener()

        viewModel = ViewModelProvider(
            requireActivity(),
            viewModelFactory
        ).get(MovieListViewModel::class.java)

        viewModel.favoriteMovies.observe(viewLifecycleOwner, Observer { favoriteMovies ->
            recyclerAdapter.setItems(favoriteMovies)

            if (favoriteMovies.isEmpty()) {
                listener.onCloseFragment()
            }
        })
    }

    private fun initRecycler() {
        recyclerAdapter = FavoriteMovieAdapter(LayoutInflater.from(context))

        recyclerView = requireView().findViewById(R.id.favoriteRecyclerView)
        recyclerView.apply {
            adapter = recyclerAdapter
            itemAnimator = CustomItemAnimator()
        }
    }

    private fun initSwipeDeleteListener() {
        ItemTouchHelper(
        object : SimpleCallback(
            UP or DOWN,
            LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: ViewHolder, target: ViewHolder
            ): Boolean {
                val fromPos = viewHolder.adapterPosition
                val toPos = target.adapterPosition
                // move item in `fromPos` to `toPos` in adapter.
                return true // true if moved, false otherwise
            }

            override fun onSwiped(
                viewHolder: ViewHolder,
                direction: Int
            ) {
                val items = recyclerAdapter.getItems()
                val adapterItem = items[viewHolder.adapterPosition]
                viewModel.deleteFavoriteMovieById(adapterItem.id)
            }
        }).attachToRecyclerView(recyclerView)
    }
}
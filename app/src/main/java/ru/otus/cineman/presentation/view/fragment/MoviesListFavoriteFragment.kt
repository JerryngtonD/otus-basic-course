package ru.otus.cineman.presentation.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.otus.cineman.R
import ru.otus.cineman.presentation.view.adapter.FavoriteMovieAdapter
import ru.otus.cineman.presentation.view.animation.CustomItemAnimator
import ru.otus.cineman.presentation.viewmodel.MovieListViewModel
import ru.otus.cineman.presentation.viewmodel.ViewModelFactory


class MoviesListFavoriteFragment : Fragment() {
    companion object {
        const val TAG = "MOVIES_LIST_FAVORITE"
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: FavoriteMovieAdapter
    private lateinit var viewModel: MovieListViewModel
    private lateinit var viewModelFactory: ViewModelFactory


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite_movies, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecycler()
        initSwipeDeleteListener()

        viewModelFactory = ViewModelFactory(context = null)
        viewModel = ViewModelProvider(
            requireActivity(),
            viewModelFactory
        ).get(MovieListViewModel::class.java)

        viewModel.favoriteMovies.observe(viewLifecycleOwner, Observer { favoriteMovies ->
            recyclerAdapter.setItems(favoriteMovies)
        })
    }

    private fun initRecycler() {
        recyclerAdapter = FavoriteMovieAdapter(LayoutInflater.from(context))

        val itemDecoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.divider,
                null
            )!!
        )
        recyclerView = requireView().findViewById(R.id.favoriteRecyclerView)
        recyclerView.apply {
            adapter = recyclerAdapter
            itemAnimator = CustomItemAnimator()
            addItemDecoration(itemDecoration)
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
                    viewModel.onDeleteFavoriteMovieById(adapterItem.id)
                }
            }).let {
            it.attachToRecyclerView(recyclerView)
        }
    }
}
package ru.otus.cineman.presentation.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat.getDrawable
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import ru.otus.cineman.R
import ru.otus.cineman.data.entity.json.MovieModel
import ru.otus.cineman.presentation.view.adapter.MovieItemAdapter
import ru.otus.cineman.presentation.view.animation.CustomItemAnimator
import ru.otus.cineman.presentation.viewmodel.MovieListViewModel
import ru.otus.cineman.presentation.viewmodel.ViewModelFactory


class MoviesListFragment : Fragment() {
    companion object {
        const val TAG = "MOVIES_LIST_FRAGMENT"
    }

    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var moviesListViewModel: MovieListViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var coordinatorLayout: View
    private lateinit var recyclerAdapter: MovieItemAdapter
    private lateinit var moviesListListener: MovieListListener
    private lateinit var progressBar: ProgressBar


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        if (activity is MovieListListener) {
            moviesListListener = activity as MovieListListener
        }
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movies_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initRecycler()
        progressBar = view.findViewById(R.id.progress_bar)
        coordinatorLayout = requireActivity().findViewById(R.id.coordinatorMovies)
        viewModelFactory = ViewModelFactory(context = null)
        moviesListViewModel = ViewModelProvider(
            requireActivity(),
            viewModelFactory
        ).get(MovieListViewModel::class.java)

        moviesListViewModel.movies.observe(viewLifecycleOwner,
            Observer { movies ->
                recyclerAdapter.setItems(movies)
            })

        moviesListViewModel.error.observe(
            viewLifecycleOwner,
            Observer { error ->
                Log.i(TAG, error)
                val errorText = "$error\n\n${resources.getString(R.string.fault_loading_movies)}"
                val snackbar = Snackbar.make(coordinatorLayout, errorText, Snackbar.LENGTH_INDEFINITE)
                    .setAction(resources.getString(R.string.retry)) { moviesListViewModel.onGetMovies() }
                val snackBarView = snackbar.view
                val textView = snackBarView.findViewById<TextView>(R.id.snackbar_text)
                textView.setLines(5)
                snackbar.show()
            })

        moviesListViewModel.isLoading.observe(
            viewLifecycleOwner,
            Observer { isLoading ->
                if (isLoading) {
                    showProgressBar()
                } else {
                    dismissProgressBar()
                }
            })

        moviesListViewModel.isInitiallyViewed.observe(viewLifecycleOwner,
            Observer { isInitiallyViewed ->
                if (!isInitiallyViewed) {
                    moviesListViewModel.onGetMovies()
                    moviesListViewModel.onInitialViewed()
                }
            })

        setOnScrollListener()
        setSwipeRefreshListener()
    }

    private fun initRecycler() {
        recyclerAdapter = MovieItemAdapter(LayoutInflater.from(context), object :
            MovieItemAdapter.OnMovieClickListener {

            override fun onDetailsClick(movie: MovieModel) {
                moviesListViewModel.onMovieSelect(movie)
                moviesListListener.onDetailsClick()
            }

            override fun onChangeFavoriteStatus(movie: MovieModel) {
                moviesListViewModel.onChangeFavoriteStatus(movie.id)
                val text = if (movie.isFavorite) R.string.success_added_to_favorites
                                else R.string.success_removed_from_favorites
                val snackbar = Snackbar.make(coordinatorLayout, text, Snackbar.LENGTH_LONG)
                    .setAction(resources.getString(R.string.undo_title)) {
                        moviesListViewModel.onChangeFavoriteStatus(movie.id)
                        val snackbarUndo = Snackbar.make(
                            coordinatorLayout,
                            resources.getString(R.string.undo_text),
                            Snackbar.LENGTH_LONG
                        )
                        snackbarUndo.show()
                    }
                snackbar.show()
            }
        })

        val itemDecoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(getDrawable(resources, R.drawable.divider, null)!!)

        recyclerView = requireView().findViewById(R.id.recyclerView)
        recyclerView.apply {
            adapter = recyclerAdapter
            itemAnimator = CustomItemAnimator()
            addItemDecoration(itemDecoration)
        }
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun dismissProgressBar() {
        progressBar.visibility = View.GONE
    }

    private fun isLastItemDisplaying(recyclerView: RecyclerView): Boolean {
        if (recyclerAdapter.itemCount != 0) {
            val lastVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager)
                .findLastVisibleItemPosition()

            if (lastVisibleItemPosition != RecyclerView.NO_POSITION &&
                lastVisibleItemPosition == (recyclerView.adapter!!.itemCount - 1)
            ) {
                return true
            }
        }
        return false
    }

    private fun setOnScrollListener() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isLastItemDisplaying(recyclerView)) {
                    Log.d(TAG, "LoadMore")
                    moviesListViewModel.onLoadMoreMovies()
                }
            }
        })
    }

    private fun setSwipeRefreshListener() {
        requireView().findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
            ?.let {
                it.setOnRefreshListener {
                    moviesListViewModel.onRefreshMovies()
                    it.isRefreshing = false
                }
            }
    }
}

interface MovieListListener {
    fun onDetailsClick()
}
package ru.otus.cineman.presentation.view.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat.getDrawable
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerFragment
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import ru.otus.cineman.R
import ru.otus.cineman.data.entity.FavoriteMovieModel
import ru.otus.cineman.data.entity.MovieModel
import ru.otus.cineman.data.entity.WatchLaterMovieModel
import ru.otus.cineman.di.modules.ViewModelFactory
import ru.otus.cineman.presentation.view.adapter.MovieItemAdapter
import ru.otus.cineman.presentation.view.adapter.OnSearchMovieClickListener
import ru.otus.cineman.presentation.view.adapter.SearchMovieAdapter
import ru.otus.cineman.presentation.view.animation.CustomItemAnimator
import ru.otus.cineman.presentation.viewmodel.MovieListViewModel
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class MoviesListFragment :  DaggerFragment() {
    companion object {
        const val TAG = "MOVIES_LIST_FRAGMENT"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val moviesListViewModel: MovieListViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            viewModelFactory
        ).get(MovieListViewModel::class.java)
    }

    private lateinit var moviesRecyclerView: RecyclerView
    private lateinit var coordinatorLayout: View
    private lateinit var recyclerAdapter: MovieItemAdapter
    private lateinit var moviesListListener: MovieListListener
    private lateinit var progressBar: ProgressBar

    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var searchAdapter: SearchMovieAdapter
    private lateinit var searchMoviesText: SearchView

    private var favoritesMoviesList = mutableListOf<FavoriteMovieModel>()
    private var watchLaterMoviesList = mutableListOf<WatchLaterMovieModel>()


    override fun onPause() {
        super.onPause()
        moviesListViewModel.getFromCache = true
    }

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
        initMoviesRecycler()
        progressBar = view.findViewById(R.id.progress_bar)
        coordinatorLayout = requireActivity().findViewById(R.id.coordinatorMovies)

        initSearchRecycler()
        searchMoviesText = view.findViewById(R.id.searchMoviesText)



        moviesListViewModel.isLoading.observe(
            viewLifecycleOwner,
            Observer { isLoading ->
                if (isLoading) {
                    showProgressBar()
                } else {
                    dismissProgressBar()
                }
            })



        moviesListViewModel.error.observe(
            viewLifecycleOwner,
            Observer { error ->
                if (error != null) {
                    Log.i(TAG, error)
                    moviesListViewModel.setErrorLoading(null)
                    val errorText = "$error"
//                    val errorText =
//                        "$error\n\n${resources.getString(R.string.fault_loading_movies)}"
                    val snackbar =
                        Snackbar.make(coordinatorLayout, errorText, Snackbar.LENGTH_LONG)
                            .setAction(resources.getString(R.string.retry)) {
                                moviesListViewModel.onGetMovies(
                                    true
                                )
                            }
                    val snackBarView = snackbar.view
                    val textView = snackBarView.findViewById<TextView>(R.id.snackbar_text)
                    textView.setLines(5)
                    snackbar.show()
                }
            })

        moviesListViewModel.favoriteMovies.observe(
            viewLifecycleOwner,
            Observer { favorites ->
                favoritesMoviesList.clear()
                favoritesMoviesList.addAll(favorites)
            }
        )

        moviesListViewModel.searchedMovies.observe(
            viewLifecycleOwner,
            Observer { searchedMovies ->
                searchAdapter.setItems(searchedMovies)
            }
        )

        moviesListViewModel.watchLaterMovies.observe(viewLifecycleOwner,
            Observer { watchLaterMovies ->

                watchLaterMoviesList.clear()
                watchLaterMoviesList.addAll(watchLaterMovies)
            })

        moviesListViewModel.movies.observe(viewLifecycleOwner, Observer { cachedFilms ->
            if (cachedFilms.isEmpty()) {
                moviesListViewModel.onGetMovies(true)
                return@Observer
            }
            val list: List<MovieModel>

            if (moviesListViewModel.getFromCache) {
                list = cachedFilms
                moviesListViewModel.getFromCache = false
            } else {
                list = cachedFilms.takeLast(20)
            }

            list.forEach { cachedFilm ->
                cachedFilm.isFavorite = false
                favoritesMoviesList.forEach { favoriteFilm ->
                    if (cachedFilm.title == favoriteFilm.title) {
                        cachedFilm.isFavorite = true
                    }
                }


                watchLaterMoviesList.forEach { watchLaterFilm ->
                    if (cachedFilm.title == watchLaterFilm.title) {
                        cachedFilm.isWatchLater = true
                        cachedFilm.watchTime = watchLaterFilm.timeOfNotification
                    }
                }
            }

            recyclerAdapter.setItemsWithoutDiff(list)
        })


        setBackPressedListener()
        setOnScrollListener()
        setSwipeRefreshListener()
        setSearchTypingMoviesListener()
        hideSoftKeyboardOnScroll(searchRecyclerView)
        hideSoftKeyboardOnScroll(moviesRecyclerView)
    }

    private fun initSearchRecycler() {
        searchAdapter =
            SearchMovieAdapter(LayoutInflater.from(context), object : OnSearchMovieClickListener {
                override fun onSearchDetailsClick(movie: MovieModel) {
                    moviesListViewModel.onMovieSelect(movie)
                    moviesListListener.onDetailsClick()
                }
            })

        val itemDecoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(getDrawable(resources, R.drawable.divider, null)!!)

        searchRecyclerView = requireView().findViewById(R.id.searchMoviesRecycler)
        searchRecyclerView.apply {
            adapter = searchAdapter
            itemAnimator = CustomItemAnimator()
            addItemDecoration(itemDecoration)
        }
    }

    private fun initMoviesRecycler() {
        recyclerAdapter = MovieItemAdapter(LayoutInflater.from(context), object :
            MovieItemAdapter.OnMovieClickListener {

            override fun onDetailsClick(movie: MovieModel) {
                moviesListViewModel.onMovieSelect(movie)
                moviesListListener.onDetailsClick()
            }

            override fun onChangeFavoriteStatus(position: Int) {
                val movie = recyclerAdapter.items[position]
                val text = if (movie.isFavorite) R.string.success_removed_from_favorites
                else R.string.success_added_to_favorites

                val currentIsFavorite = movie.isFavorite

                moviesListViewModel.onChangeFavoriteStatus(movie.id, currentIsFavorite)

                movie.isFavorite = !movie.isFavorite

                recyclerAdapter.notifyItemChanged(position)

                val snackbar = Snackbar.make(coordinatorLayout, text, Snackbar.LENGTH_LONG)
                    .setAction(resources.getString(R.string.undo_title)) {
                        moviesListViewModel.onChangeFavoriteStatus(movie.id, !currentIsFavorite)
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

        moviesRecyclerView = requireView().findViewById(R.id.moviesRecycler)
        moviesRecyclerView.apply {
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

    private fun setSearchTypingMoviesListener() {
        searchMoviesText.setOnClickListener {
            searchMoviesText.isIconified = false
        }

        val subscribeBy = Observable.create(ObservableOnSubscribe<String> { subscriber ->
            searchMoviesText.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    subscriber.onNext(query)
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    searchAdapter.clearItems()

                    if (newText.isEmpty()) {
                        searchRecyclerView.isVisible = false
                        moviesRecyclerView.isVisible = true
                    } else {
                        searchRecyclerView.isVisible = true
                        moviesRecyclerView.isVisible = false
                    }
                    subscriber.onNext(newText)
                    return false
                }
            })
        }).subscribeOn(Schedulers.computation())
            .map {
                it.toLowerCase(Locale.getDefault()).trim()
            }
            .debounce(500, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .filter { it.isNotBlank() }
            .retry(3)
            .subscribeBy(
                onNext = {
                    moviesListViewModel.searchMoviesByText(it)
                },
                onError = {
                    Log.e(TAG, it.localizedMessage)
                }
            )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun hideSoftKeyboardOnScroll(recyclerView: RecyclerView) {
        recyclerView.setOnTouchListener { v, event ->
            val imm: InputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(recyclerView.windowToken, 0)
            false
        }
    }

    private fun isLastItemDisplaying(recyclerView: RecyclerView): Boolean {
        if (recyclerView.adapter?.itemCount != 0) {
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
        moviesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isLastItemDisplaying(recyclerView) && dy > 0 && moviesListViewModel.needLoading) {
                    Log.d(TAG, "LoadMore")
                    moviesListViewModel.onLoadMoreMovies()
                    moviesListViewModel.needLoading = false
                }
            }
        })
    }

    private fun setSwipeRefreshListener() {
        requireView().findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
            ?.let {
                it.setOnRefreshListener {
                    recyclerAdapter.items.clear()
                    moviesListViewModel.onGetMovies(true)
                    it.isRefreshing = false
                }
            }
    }

    private fun setBackPressedListener() {
        val callback = object : OnBackPressedCallback(
            true
        ) {
            override fun handleOnBackPressed() {
                if (searchMoviesText.query.isNotEmpty()) {
                    searchAdapter.clearItems()
                    searchMoviesText.setQuery("", false)
                    searchMoviesText.clearFocus()
                    searchRecyclerView.isVisible = false
                    moviesRecyclerView.isVisible = true
                } else {
                    moviesListListener.onBackPressedByMoviesList()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}

interface MovieListListener {
    fun onDetailsClick()
    fun onBackPressedByMoviesList()
}
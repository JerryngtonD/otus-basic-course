package ru.otus.cineman.presentation.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import ru.otus.cineman.R
import ru.otus.cineman.data.entity.json.MovieModel
import ru.otus.cineman.presentation.view.activity.MainActivity
import ru.otus.cineman.presentation.view.adapter.MovieItemAdapter
import ru.otus.cineman.presentation.viewmodel.MovieListViewModel


class MoviesListFragment : Fragment() {
    private lateinit var viewModel: MovieListViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MovieItemAdapter
    private lateinit var moviesListListener: MovieListListener


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

        viewModel = ViewModelProvider(activity!!).get(MovieListViewModel::class.java)

        viewModel.movies.observe(viewLifecycleOwner,
            Observer { movies ->
                adapter.setItems(movies)
            })

        viewModel.error.observe(
            viewLifecycleOwner,
            Observer { error ->
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            })

        viewModel.isInitiallyViewed.observe(viewLifecycleOwner,
            Observer { isInitiallyViewed ->
            run {
                if (!isInitiallyViewed) {
                    viewModel.onGetMovies()
                    viewModel.onInitialViewed()
                }
            }
        })
    }

    private fun initRecycler() {
        adapter = MovieItemAdapter(LayoutInflater.from(context), object :
            MovieItemAdapter.OnMovieClickListener {

            override fun onDetailsClick(movie: MovieModel) {
                viewModel.onMovieSelect(movie)
                moviesListListener.onDetailsClick()
            }

            override fun onChangeFavoriteStatus(movie: MovieModel) {
                viewModel.onChangeFavoriteStatus(movie.id)
            }
        })
        recyclerView = view!!.findViewById(R.id.recyclerView)
        recyclerView.adapter = adapter
    }
}
//    var listener: MovieListListener? = null
//    var coordinatorLayout: View? = null
//    var recycler: RecyclerView? = null
//    var progressBar: ProgressBar? = null
//
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        listener = activity as? MovieListListener
//        super.onActivityCreated(savedInstanceState)
//    }
//
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        coordinatorLayout = activity?.findViewById(R.id.coordinatorMovies)
//        val itemDecoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
//        itemDecoration.setDrawable(getDrawable(resources, R.drawable.divider, null)!!)
//        recycler = view.findViewById<RecyclerView>(R.id.recyclerView)
//            .apply {
//                adapter = createAdapter(view)
//                itemAnimator =
//                    CustomItemAnimator()
//                addItemDecoration(itemDecoration)
//            }
//        setOnScrollListener()
//        setSwipeRefreshListener(view)
//        val isSelectedMovieUpdated =
//            arguments?.getBoolean(IS_PREVIEW_MOVIES_UPDATED_BY_DETAILS) ?: false
//        if (isSelectedMovieUpdated) {
//            val comment = arguments?.getString(UPDATED_COMMENT)
//            val isLikedStatus = arguments?.getBoolean(UPDATED_IS_LIKED_STATUS)
//            updateSelectedMovieDetailsInfo(comment, isLikedStatus)
//        }
//        if (IS_INIT_LOADING) {
//            IS_INIT_LOADING = false
//            getDataFromServer()
//        }
//    }
//
//    private fun createAdapter(view: View): MovieItemAdapter {
//        return MovieItemAdapter(
//            LayoutInflater.from(activity),
//            getMovieStorage(),
//            object :
//                MovieItemAdapter.OnMovieClickListener {
//
//                override fun onMoreClick(movieItem: MovieItem) {
//                    selectNewItem(movieItem)
//                    listener?.onMoreClick(movieItem)
//                }
//
//                override fun onChangeFavoriteStatus(movieItem: MovieItem) {
//                    showSnackBarWithUpdateMovie(movieItem)
//                }
//
//                private fun selectNewItem(movieItem: MovieItem) {
//                    val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
//                    val adapter =
//                        recyclerView.adapter as MovieItemAdapter
//                    unselectAllItems()
//                    movieItem.isSelected = true
//                    adapter.notifyDataSetChanged()
//                }
//
//                private fun unselectAllItems() {
//                    val movieItemAdapter =
//                        recycler?.adapter as MovieItemAdapter
//                    movieItemAdapter.items.forEach {
//                        it.isSelected = false
//                    }
//                }
//            })
//    }
//
//    private fun showSnackBarWithUpdateMovie(movieItem: MovieItem) {
//        val movieItemAdapter = recycler?.adapter as MovieItemAdapter
//        val updatedMoviePosition = movieItemAdapter.items.indexOf(movieItem)
//        val updatedMovie = movieItemAdapter.items[updatedMoviePosition]
//
//        changeMovieFavoriteStatus(updatedMovie)
//        updateFavoriteMovies(updatedMovie)
//        movieItemAdapter.notifyItemChanged(updatedMoviePosition)
//
//        val text =
//            if (updatedMovie.isFavorite) R.string.success_added_to_favorites else R.string.success_removed_from_favorites
//        val snackbar = Snackbar.make(coordinatorLayout!!, text, Snackbar.LENGTH_LONG)
//            .setAction(resources.getString(R.string.undo_title)) {
//                changeMovieFavoriteStatus(updatedMovie)
//                updateFavoriteMovies(updatedMovie)
//                movieItemAdapter.notifyItemChanged(updatedMoviePosition)
//                val snackbarUndo = Snackbar.make(
//                    coordinatorLayout!!,
//                    resources.getString(R.string.undo_text),
//                    Snackbar.LENGTH_LONG
//                )
//                snackbarUndo.show()
//            }
//        snackbar.show()
//    }
//
//    private fun showSnackBarWithSuccessLoading() {
//        Snackbar.make(coordinatorLayout!!, R.string.success_loading_movies, Snackbar.LENGTH_LONG)
//            .show()
//    }
//
//    private fun showSnackBarWithFaultLoading() {
//        Snackbar.make(coordinatorLayout!!, R.string.fault_loading_movies, Snackbar.LENGTH_LONG)
//            .show()
//    }
//
//    private fun changeMovieFavoriteStatus(movieItem: MovieItem): MovieItem = movieItem.apply {
//        isFavorite = !isFavorite
//    }
//
//    private fun updateFavoriteMovies(updatedMovie: MovieItem) {
//        val favoriteMovies = getFavoriteMovieStorage()
//        if (updatedMovie.isFavorite) {
//            favoriteMovies.add(updatedMovie)
//        } else {
//            favoriteMovies.remove(updatedMovie)
//        }
//    }
//
//    private fun updateSelectedMovieDetailsInfo(comment: String?, isLikedStatus: Boolean?) {
//        val movieItemAdapter = recycler?.adapter as MovieItemAdapter
//        val selectedMovie = movieItemAdapter.items.first { it.isSelected }
//
//        val isUpdatedMovieDetails =
//            !(selectedMovie.comment == comment && selectedMovie.isLiked == isLikedStatus)
//
//        if (isUpdatedMovieDetails) {
//            val positionSelectedMovie = movieItemAdapter.items.indexOf(selectedMovie)
//            selectedMovie.comment = comment
//            selectedMovie.isLiked = isLikedStatus ?: false
//            movieItemAdapter.notifyItemChanged(positionSelectedMovie)
//        }
//    }
//
//    private fun setOnScrollListener() {
//        recycler?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                if (isLastItemDisplaying(recyclerView)) {
//                    Log.d(TAG, "LoadMore")
//                    getData()
//                }
//            }
//        })
//    }
//
//    private fun setSwipeRefreshListener(view: View) {
//        view.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
//            ?.let {
//                it.setOnRefreshListener {
//                    getMovieStorage().clear()
//                    MOVIES_PAGE = 1
//                    recycler?.adapter?.notifyDataSetChanged()
//                    getDataFromServer()
//                    it.isRefreshing = false
//                }
//            }
//    }
//
//    private fun getData() {
//        MOVIES_PAGE++
//        getDataFromServer()
//    }
//
//    private fun getDataFromServer() {
//        showProgressBar(view!!)
//        App.movieService.getPopularMovies(MOVIES_PAGE)
//            .enqueue(object : Callback<MoviesResult?> {
//                override fun onFailure(call: Call<MoviesResult?>, t: Throwable) {
//                    dismissProgressBar(view!!)
//                    showSnackBarWithFaultLoading()
//                }
//
//                override fun onResponse(
//                    call: Call<MoviesResult?>,
//                    response: Response<MoviesResult?>
//                ) {
//
//                    if (response.isSuccessful) {
//                        dismissProgressBar(view!!)
//                        showSnackBarWithSuccessLoading()
//                        response.body()?.results?.map {
//                            MovieItem(
//                                title = it.title,
//                                description = it.description,
//                                image = "$IMAGE_URL${it.image}"
//                            )
//                        }?.let {
//                            getMovieStorage().addAll(it)
//                            progressBar?.visibility = View.INVISIBLE
//                            recycler?.adapter?.notifyDataSetChanged()
//
//                        }
//                    }
//                }
//            })
//    }
//
//    private fun isLastItemDisplaying(recyclerView: RecyclerView): Boolean {
//        if (recyclerView.adapter?.itemCount != 0) {
//            val lastVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager)
//                .findLastVisibleItemPosition()
//
//            if (lastVisibleItemPosition != RecyclerView.NO_POSITION &&
//                lastVisibleItemPosition == (recyclerView.adapter!!.itemCount - 1)
//            ) {
//                return true
//            }
//        }
//        return false
//    }
//
//    private fun showProgressBar(view: View) {
//        progressBar = view.findViewById(R.id.progress_bar)
//        progressBar?.visibility = View.VISIBLE
//    }
//
//    private fun dismissProgressBar(view: View) {
//        progressBar = view.findViewById(R.id.progress_bar)
//        progressBar?.visibility = View.INVISIBLE
//    }
//}

interface MovieListListener {
    fun onDetailsClick()
}
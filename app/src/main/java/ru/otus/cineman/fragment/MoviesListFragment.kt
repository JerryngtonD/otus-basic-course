package ru.otus.cineman.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat.getDrawable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.otus.cineman.MovieStorage
import ru.otus.cineman.MovieStorage.Companion.IMAGE_URL
import ru.otus.cineman.MovieStorage.Companion.IS_INIT_LOADING
import ru.otus.cineman.MovieStorage.Companion.getFavoriteMovieStorage
import ru.otus.cineman.MovieStorage.Companion.getMovieStorage
import ru.otus.cineman.R
import ru.otus.cineman.activity.MainActivity.Companion.IS_PREVIEW_MOVIES_UPDATED_BY_DETAILS
import ru.otus.cineman.activity.MainActivity.Companion.UPDATED_COMMENT
import ru.otus.cineman.activity.MainActivity.Companion.UPDATED_IS_LIKED_STATUS
import ru.otus.cineman.adapter.MovieItemAdapter
import ru.otus.cineman.animation.CustomItemAnimator
import ru.otus.cineman.model.MovieItem
import ru.otus.cineman.model.json.MoviesResult
import java.util.*

class MoviesListFragment : Fragment() {
    companion object {
        const val ANIMATE_INDEX_POSITION = 1

        const val NIGHT_MODE_PREFERENCES = "NIGHT_MODE_PREFS"
        const val KEY_IS_NIGHT_MODE = "IS_NIGHT_MODE"
    }

    private lateinit var sharedPreferences: SharedPreferences

    var listener: MovieListListener? = null
    var coordinatorLayout: View? = null
    var isNightMode = false

    var recycler: RecyclerView? = null

    var progressBar: ProgressBar? = null
    var dayNightModeButton: View? = null
    var addNewButton: View? = null


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        listener = activity as? MovieListListener
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movies_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        coordinatorLayout = activity?.findViewById(R.id.coordinatorMovies)
        val itemDecoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(getDrawable(resources, R.drawable.divider, null)!!)
        recycler = view.findViewById<RecyclerView>(R.id.recyclerView)
            .apply {
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

        setAddNewMovieListener(view)
        processThemeMode(view)

        if (IS_INIT_LOADING) {
            IS_INIT_LOADING = false
            showProgressBar(view)
            MovieStorage.api.getPopularFilms()
                .enqueue(object : Callback<MoviesResult?> {
                    override fun onFailure(call: Call<MoviesResult?>, t: Throwable) {
                        print("some error")
                        dismissProgressBar(view)
                    }

                    override fun onResponse(
                        call: Call<MoviesResult?>,
                        response: Response<MoviesResult?>
                    ) {
                        if (response.isSuccessful) {
                            dismissProgressBar(view)
                            getMovieStorage().clear()

                            response.body()?.results?.map {
                                MovieItem(
                                    title = it.title,
                                    description = it.description,
                                    image = "$IMAGE_URL${it.image}"
                                )
                            }?.let {
                                getMovieStorage().addAll(it)
                                progressBar?.visibility = View.INVISIBLE
                                addNewButton?.visibility = View.VISIBLE
                                dayNightModeButton?.visibility = View.VISIBLE

                                recycler?.adapter?.notifyDataSetChanged()

                            }
                        }
                    }
                })
        }
    }

    private fun createAdapter(view: View): MovieItemAdapter {
        return MovieItemAdapter(
            LayoutInflater.from(activity),
            getMovieStorage(),
            object : MovieItemAdapter.OnMovieCLickListener {

                override fun onMoreClick(movieItem: MovieItem) {
                    selectNewItem(movieItem)
                    listener?.onMoreClick(movieItem)
                }

                override fun onChangeFavoriteStatus(movieItem: MovieItem) {
                    showSnackBar(movieItem)
                }

                private fun selectNewItem(movieItem: MovieItem) {
                    val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
                    val adapter = recyclerView.adapter as MovieItemAdapter
                    unselectAllItems()
                    movieItem.isSelected = true
                    adapter.notifyDataSetChanged()
                }

                private fun unselectAllItems() {
                    val movieItemAdapter = recycler?.adapter as MovieItemAdapter
                    movieItemAdapter.items.forEach {
                        it.isSelected = false
                    }
                }
            })
    }

    private fun showSnackBar(movieItem: MovieItem) {
        val movieItemAdapter = recycler?.adapter as MovieItemAdapter
        val updatedMoviePosition = movieItemAdapter.items.indexOf(movieItem)
        val updatedMovie = movieItemAdapter.items[updatedMoviePosition]

        changeMovieFavoriteStatus(updatedMovie)
        updateFavoriteMovies(updatedMovie)
        movieItemAdapter.notifyItemChanged(updatedMoviePosition)

        val text =
            if (updatedMovie.isFavorite) R.string.success_added_to_favorites else R.string.success_removed_from_favorites
        val snackbar = Snackbar.make(coordinatorLayout!!, text, Snackbar.LENGTH_LONG)
            .setAction(resources.getString(R.string.undo_title)) {
                changeMovieFavoriteStatus(updatedMovie)
                updateFavoriteMovies(updatedMovie)
                movieItemAdapter.notifyItemChanged(updatedMoviePosition)
                val snackbarUndo = Snackbar.make(
                    coordinatorLayout!!,
                    resources.getString(R.string.undo_text),
                    Snackbar.LENGTH_LONG
                )
                snackbarUndo.show()
            }
        snackbar.show()
    }

    private fun changeMovieFavoriteStatus(movieItem: MovieItem): MovieItem = movieItem.apply {
        isFavorite = !isFavorite
    }

    private fun updateFavoriteMovies(updatedMovie: MovieItem) {
        val favoriteMovies = getFavoriteMovieStorage()
        if (updatedMovie.isFavorite) {
            favoriteMovies.add(updatedMovie)
        } else {
            favoriteMovies.remove(updatedMovie)
        }
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

    private fun setAddNewMovieListener(view: View) {
        addNewButton = view.findViewById<View>(R.id.add_new)
        val adapter = recycler?.adapter as MovieItemAdapter
        addNewButton?.setOnClickListener {
            val newGeneratedMovie = MovieItem(
                title = resources.getString(R.string.incognito_title) + UUID.randomUUID().toString()
                    .take(5),
//                image = R.drawable.incognito,
                description = resources.getString(R.string.incognito_description)
            )
            adapter.add(ANIMATE_INDEX_POSITION, newGeneratedMovie)
        }
    }

    private fun saveNightModeState(isCheckedNightMode: Boolean) {
        sharedPreferences.edit().apply {
            putBoolean(KEY_IS_NIGHT_MODE, isCheckedNightMode)
        }.apply()
    }

    private fun checkNightModeIsActivated() {
        if (sharedPreferences.getBoolean(KEY_IS_NIGHT_MODE, false)) {
            AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES
            )
            isNightMode = true
        } else {
            AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO
            )
            isNightMode = false
        }
    }

    private fun showProgressBar(view: View) {
        progressBar = view.findViewById(R.id.progress_bar)
        progressBar?.visibility = View.VISIBLE
    }

    private fun dismissProgressBar(view: View) {
        progressBar = view.findViewById(R.id.progress_bar)
        progressBar?.visibility = View.INVISIBLE
    }

    private fun processThemeMode(view: View) {
        val dayNightModeListener = View.OnClickListener {
            if (isNightMode) {
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
                )
                saveNightModeState(false)
            } else {
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
                )
                saveNightModeState(true)
            }
            activity?.recreate()
        }

        sharedPreferences =
            activity?.getSharedPreferences(NIGHT_MODE_PREFERENCES, Context.MODE_PRIVATE)
                ?: throw Exception("Can't proceed light mode")
        dayNightModeButton = view.findViewById<View>(R.id.day_night_mode)
        dayNightModeButton?.setOnClickListener(dayNightModeListener)

        checkNightModeIsActivated()
    }
}

interface MovieListListener {
    fun onMoreClick(movieItem: MovieItem)
    fun openFavoriteMovies()
}
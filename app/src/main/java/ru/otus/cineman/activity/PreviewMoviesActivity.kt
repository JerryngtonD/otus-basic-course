package ru.otus.cineman.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ru.otus.cineman.R
import ru.otus.cineman.fragment.*
import ru.otus.cineman.model.MovieItem


class PreviewMoviesActivity : AppCompatActivity(), MovieListListener {
    companion object {
        const val TAG = "MovieListFragment"
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        if (fragment is MoviesListFragment) {
            fragment.listener = this
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, MoviesListFragment(), TAG)
            .commit()
    }

    private fun openMovieDetailsFragment(movieItem: MovieItem) {
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragmentContainer, MovieDetailsFragment.newInstance(movieItem).apply {
                    listener = supportFragmentManager.findFragmentByTag(TAG) as MoviesListFragment
                }, MovieDetailsFragment.TAG
            )
            .addToBackStack(null)
            .commit()
    }

    override fun onMoreClick(movieItem: MovieItem) {
        openMovieDetailsFragment(movieItem)
    }

    override fun onBackPressed() {
        checkBackIsPressedInFragments()
        super.onBackPressed()
    }

    private fun checkBackIsPressedInFragments() {
        val fragments =
            supportFragmentManager.fragments
        for (f in fragments) {
            if (f != null && f is BaseFragment) f.onBackPressed()
        }
    }
    //    companion object : KLogging() {
//        const val FILM_DETAILS_REQUEST_CODE = 12345
//
//        // Заголовки при отправке дополнительной информации в/из MovieDetailsActivity
//        const val IS_LIKED = "IS_LIKED"
//        const val USER_COMMENT = "USER_COMMENT"
//        const val MOVIE_ID = "MOVIE_ID"
//        const val FAVORITE_MOVIES = "FAVORITE_MOVIES"
//
//        // Коды восстановления при пересоздании активности
//        const val FILMS_STORED = "FILMS_STORED"
//
//        const val NIGHT_MODE_PREFERENCES = "NIGHT_MODE_PREFS"
//        const val KEY_IS_NIGHT_MODE = "IS_NIGHT_MODE"
//        const val RECYCLER_LAYOUT = "RECYCLER_LAYOUT"
//
//        const val ANIMATE_INDEX_POSITION = 1
//    }
//
//    var isNightMode = false
//
//    lateinit var sharedPreferences: SharedPreferences
//    lateinit var shareButton: View
//    lateinit var favoritesButton: View
//    lateinit var addNewButton: View
//    lateinit var dayNightModeButton: View
//    lateinit var movies: MutableList<MovieItem>
//    lateinit var recycler: RecyclerView

//    private val dayNightModeListener = View.OnClickListener {
//        if (isNightMode) {
//            AppCompatDelegate.setDefaultNightMode(
//                AppCompatDelegate.MODE_NIGHT_NO
//            )
//            saveNightModeState(false)
//        } else {
//            AppCompatDelegate.setDefaultNightMode(
//                AppCompatDelegate.MODE_NIGHT_YES
//            )
//            saveNightModeState(true)
//        }
//        recreate()
//    }

//    private val favoritesClickListener = View.OnClickListener {
//        val favoriteMovies = movies.filter { it.isFavorite }
//        if (favoriteMovies.isEmpty()) {
//            AlertDialog.Builder(this).apply {
//                setMessage(resources.getText(R.string.favorites_empty))
//                    .setCancelable(false)
//                    .setPositiveButton(resources.getText(R.string.ok)) { _, _ ->
//                        logger.info { "Empty favorites" }
//                    }
//                    .create()
//                    .show()
//            }
//        } else {
//            val intentFilmFavorites =
//                Intent(this@PreviewMoviesActivity, FavoriteMoviesActivity::class.java)
//            intentFilmFavorites.putParcelableArrayListExtra(
//                FAVORITE_MOVIES,
//                ArrayList(favoriteMovies)
//            )
//            startActivity(intentFilmFavorites)
//        }
//    }

//    private val shareListener = View.OnClickListener { _ ->
//        logger.info { "Share app with your friends" }
//    }


//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.preview_movies)
//
//        movies = if (savedInstanceState != null) {
//            savedInstanceState.getParcelableArrayList<MovieItem>(FILMS_STORED)
//                ?.toMutableList() ?: throw Exception()
//        } else {
//            MovieStorage.getMovieStorage()
//        }
//
//        initRecycler()
//        processThemeMode()
//        setShareClickListener()
//        setFavoritesClickListener()
//        setAddNewButtonListener()
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == FILM_DETAILS_REQUEST_CODE) {
//            if (resultCode != Activity.RESULT_OK) {
//                return;
//            }
//            val filmId =
//                data?.getIntExtra(MOVIE_ID, Int.MAX_VALUE) ?: throw IllegalArgumentException()
//            if (filmId == Int.MAX_VALUE) {
//                throw InstantiationException()
//            }
//
//            val comment = data.extras?.getString(USER_COMMENT)
//            val isLiked = data.getBooleanExtra(IS_LIKED, false)
//
//            movies[filmId].isLiked = isLiked
//            movies[filmId].comment = comment ?: ""
//        }
//    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        val filmsToSave: ArrayList<MovieItem> = ArrayList(movies)
//        outState.putParcelableArrayList(FILMS_STORED, filmsToSave)
//        outState.putParcelable(RECYCLER_LAYOUT, recycler.layoutManager?.onSaveInstanceState())
//    }
//
//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        recycler.layoutManager?.onRestoreInstanceState(savedInstanceState.getParcelable(RECYCLER_LAYOUT))
//        super.onRestoreInstanceState(savedInstanceState)
//    }

//    override fun onBackPressed() {
//        AlertDialog.Builder(this).apply {
//            setMessage(resources.getText(R.string.need_exit))
//                .setCancelable(false)
//                .setPositiveButton(resources.getText(R.string.yes_answer)) { _, _ ->
//                    finish()
//                }
//                .setNegativeButton(resources.getText(R.string.no_answer)) { dialog, _ ->
//                    dialog.dismiss()
//                }
//                .create()
//                .show()
//        }
//    }
//
//    private fun checkNightModeIsActivated() {
//        if (sharedPreferences.getBoolean(KEY_IS_NIGHT_MODE, false)) {
//            AppCompatDelegate.setDefaultNightMode(
//                AppCompatDelegate.MODE_NIGHT_YES
//            )
//            isNightMode = true
//        } else {
//            AppCompatDelegate.setDefaultNightMode(
//                AppCompatDelegate.MODE_NIGHT_NO
//            )
//            isNightMode = false
//        }
//    }
//
//    private fun saveNightModeState(isCheckedNightMode: Boolean) {
//        sharedPreferences.edit().apply {
//            putBoolean(KEY_IS_NIGHT_MODE, isCheckedNightMode)
//        }.apply()
//    }

//    private fun initRecycler() {
//        recycler = findViewById(R.id.recyclerView)
//        val layoutManager = getLayoutManager()
//        recycler.layoutManager = layoutManager
//
//        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
//        itemDecoration.setDrawable(getDrawable(R.drawable.divider)!!)
//        recycler.addItemDecoration(itemDecoration)
//        recycler.itemAnimator = CustomItemAnimator()
//        recycler.adapter = createAdapter(recycler)
//    }
//
//    private fun getLayoutManager(): RecyclerView.LayoutManager {
//        val currentOrientation = resources.configuration.orientation
//        return if (currentOrientation == Configuration.ORIENTATION_PORTRAIT)
//            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//        else GridLayoutManager(this, 2)
//    }


//    private fun processThemeMode() {
//        sharedPreferences = getSharedPreferences(NIGHT_MODE_PREFERENCES, Context.MODE_PRIVATE)
//        dayNightModeButton = findViewById(R.id.day_night_mode)
//        dayNightModeButton.setOnClickListener(dayNightModeListener)
//
//        checkNightModeIsActivated()
//    }

//    private fun setShareClickListener() {
//        shareButton = findViewById(R.id.share)
//        shareButton.setOnClickListener(shareListener)
//    }
//
//    private fun setFavoritesClickListener() {
//        favoritesButton = findViewById(R.id.favorites)
//        favoritesButton.setOnClickListener(favoritesClickListener)
//    }

//    private fun createAdapter(recycler: RecyclerView): MovieItemAdapter {
//        return MovieItemAdapter(LayoutInflater.from(this), movies, object :
//            MovieItemAdapter.OnMovieCLickListener {
//            override fun onMoreClick(position: Int) {
//                movies.forEach {
//                    it.isSelected = false
//                }
//                val selectedMovie = movies[position]
//                selectedMovie.isSelected = true
//                recycler.adapter?.notifyDataSetChanged()
//
//                val intentFilmDetails =
//                    Intent(this@PreviewMoviesActivity, MovieDetailsActivity::class.java)
//                intentFilmDetails.putExtra("film_id", position)
//                intentFilmDetails.putExtra("image_id", selectedMovie.imageId)
//                intentFilmDetails.putExtra("film_description", selectedMovie.descriptionId)
//                intentFilmDetails.putExtra("is_liked", selectedMovie.isLiked)
//                intentFilmDetails.putExtra("user_comment", selectedMovie.comment)
//                intentFilmDetails.putExtra("position", position)
//                startActivityForResult(intentFilmDetails, FILM_DETAILS_REQUEST_CODE)
//            }
//
//            override fun onChangeFavoriteStatus(position: Int) {
//                val selectedFavoriteMovie = movies[position]
//                selectedFavoriteMovie.isFavorite = !selectedFavoriteMovie.isFavorite
//                recycler.adapter?.notifyDataSetChanged()
//            }
//        })
//    }

//    private fun setAddNewButtonListener() {
//        addNewButton = findViewById(R.id.add_new)
//        addNewButton.setOnClickListener {
//            (recycler.adapter as MovieItemAdapter).add(
//                ANIMATE_INDEX_POSITION, MovieItem(
//                    titleId = R.string.incognito_title,
//                    imageId = R.drawable.incognito,
//                    descriptionId = R.string.incognito_description
//                )
//            )
//        }
//
//    }
}


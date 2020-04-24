package ru.otus.cineman.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mu.KLogging
import ru.otus.cineman.R
import ru.otus.cineman.adapter.MovieItemAdapter
import ru.otus.cineman.model.MovieItem

class PreviewMoviesActivity : AppCompatActivity() {
    companion object : KLogging() {
        const val FILMS_TITLE_TAG = "film_title"

        const val FILM_DETAILS_REQUEST_CODE = 12345

        // Заголовки при отправке дополнительной информации в/из MovieDetailsActivity
        const val IS_LIKED = "IS_LIKED"
        const val USER_COMMENT = "USER_COMMENT"
        const val MOVIE_ID = "MOVIE_ID"
        const val FAVORITE_MOVIES = "FAVORITE_MOVIES"

        // Коды восстановления при пересоздании активности
        const val FILMS_STORED = "FILMS_STORED"

        const val NIGHT_MODE_PREFERENCES = "NIGHT_MODE_PREFS"
        const val KEY_IS_NIGHT_MODE = "IS_NIGHT_MODE"
    }

    var isNightMode = false

    lateinit var sharedPreferences: SharedPreferences
    lateinit var shareButton: View
    lateinit var favoritesButton: View
    lateinit var dayNightModeButton: View
    lateinit var movies: List<MovieItem>

    private val dayNightModeListener = View.OnClickListener {
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
        recreate()
    }

    private val favoritesClickListener = View.OnClickListener {
        val favoriteMovies = movies.filter { it.isFavorite }
        if (favoriteMovies.isEmpty()) {
            AlertDialog.Builder(this).apply {
                setMessage(resources.getText(R.string.favorites_empty))
                    .setCancelable(false)
                    .setPositiveButton(resources.getText(R.string.ok)) { _, _ ->
                        logger.info { "Empty favorites" }
                    }
                    .create()
                    .show()
            }
        } else {
            val intentFilmFavorites =
                Intent(this@PreviewMoviesActivity,  FavoriteMoviesActivity::class.java)
            intentFilmFavorites.putParcelableArrayListExtra(FAVORITE_MOVIES, ArrayList(favoriteMovies))
            startActivity(intentFilmFavorites)
        }
    }

    private val shareListener = View.OnClickListener { _ ->
        logger.info { "Share app with your friends" }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.preview_movies)
        movies = initializeMovies()

        initRecycler()
        processThemeMode()
        setShareClickListener()
        setFavoritesClickListener()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        movies = savedInstanceState.getParcelableArrayList<MovieItem>(FILMS_STORED)?.toList()
            ?: emptyList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILM_DETAILS_REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK) {
                return;
            }
            val filmId =
                data?.getIntExtra(MOVIE_ID, Int.MAX_VALUE) ?: throw IllegalArgumentException()
            if (filmId == Int.MAX_VALUE) {
                throw InstantiationException()
            }

            val comment = data.extras?.getString(USER_COMMENT)
            val isLiked = data.getBooleanExtra(IS_LIKED, false)

            movies[filmId].isLiked = isLiked
            movies[filmId].comment = comment ?: ""
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val filmsToSave: ArrayList<MovieItem> = ArrayList(movies)
        outState.putParcelableArrayList(FILMS_STORED, filmsToSave)
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this).apply {
            setMessage(resources.getText(R.string.need_exit))
                .setCancelable(false)
                .setPositiveButton(resources.getText(R.string.yes_answer)) { _, _ ->
                    finish()
                }
                .setNegativeButton(resources.getText(R.string.no_answer)) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
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

    private fun saveNightModeState(isCheckedNightMode: Boolean) {
        sharedPreferences.edit().apply {
            putBoolean(KEY_IS_NIGHT_MODE, isCheckedNightMode)
        }.apply()
    }

    private fun initializeMovies(): List<MovieItem> {
        return listOf(
            MovieItem(
                titleId = R.string.spider_man_title,
                imageId = R.drawable.spiderman,
                descriptionId = R.string.spider_man_description
            ),
            MovieItem(
                titleId = R.string.hulk_title,
                imageId = R.drawable.hulk,
                descriptionId = R.string.hulk_description
            ),
            MovieItem(
                titleId = R.string.batman_title,
                imageId = R.drawable.batman,
                descriptionId = R.string.batman_description
            ),
            //---------------------------------------------------//
            MovieItem(
                titleId = R.string.antman_title,
                imageId = R.drawable.antman,
                descriptionId = R.string.antman_description
            ),
            MovieItem(
                titleId = R.string.artas_title,
                imageId = R.drawable.artas,
                descriptionId = R.string.artas_description
            ),
            MovieItem(
                titleId = R.string.asterix_title,
                imageId = R.drawable.asterix,
                descriptionId = R.string.asterix_description
            ),
            //---------------------------------------------------//
            MovieItem(
                titleId = R.string.bart_title,
                imageId = R.drawable.bart,
                descriptionId = R.string.bart_description
            ),
            MovieItem(
                titleId = R.string.batman_title,
                imageId = R.drawable.batman,
                descriptionId = R.string.batman_description
            ),
            MovieItem(
                titleId = R.string.cat_woman_title,
                imageId = R.drawable.cat_woman,
                descriptionId = R.string.cat_woman_description
            ),
            //---------------------------------------------------//
            MovieItem(
                titleId = R.string.cool_man_title,
                imageId = R.drawable.cool_man,
                descriptionId = R.string.cool_man_description
            ),
            MovieItem(
                titleId = R.string.doctor_strange_title,
                imageId = R.drawable.doctor_strange,
                descriptionId = R.string.doctor_strange_description
            ),
            MovieItem(
                titleId = R.string.gomer_title,
                imageId = R.drawable.gomer,
                descriptionId = R.string.gomer_description
            ),
            //---------------------------------------------------//
            MovieItem(
                titleId = R.string.hulk_title,
                imageId = R.drawable.hulk,
                descriptionId = R.string.hulk_description
            ),
            MovieItem(
                titleId = R.string.mummy_title,
                imageId = R.drawable.mummy,
                descriptionId = R.string.mummy_description
            ),
            MovieItem(
                titleId = R.string.spider_man_title,
                imageId = R.drawable.spiderman,
                descriptionId = R.string.spider_man_description
            ),
            //---------------------------------------------------//
            MovieItem(
                titleId = R.string.tanos_title,
                imageId = R.drawable.tanos,
                descriptionId = R.string.tanos_description
            ),
            MovieItem(
                titleId = R.string.venom_title,
                imageId = R.drawable.venom,
                descriptionId = R.string.venom_description
            )
        )
    }

    private fun initRecycler() {
        val recycler = findViewById<RecyclerView>(R.id.recyclerView)
        val layoutManager = getLayoutManager()
        recycler.layoutManager = layoutManager

        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(getDrawable(R.drawable.divider)!!)
        recycler.addItemDecoration(itemDecoration)

        recycler.adapter = MovieItemAdapter(LayoutInflater.from(this), movies, object :
            MovieItemAdapter.OnMovieCLickListener {
            override fun onMoreClick(position: Int) {
                movies.forEach {
                    it.isSelected = false
                }
                val selectedMovie = movies[position]
                selectedMovie.isSelected = true
                recycler.adapter?.notifyDataSetChanged()

                val intentFilmDetails =
                    Intent(this@PreviewMoviesActivity, MovieDetailsActivity::class.java)
                intentFilmDetails.putExtra("film_id", position)
                intentFilmDetails.putExtra("image_id", selectedMovie.imageId)
                intentFilmDetails.putExtra("film_description", selectedMovie.descriptionId)
                intentFilmDetails.putExtra("is_liked", selectedMovie.isLiked)
                intentFilmDetails.putExtra("user_comment", selectedMovie.comment)
                intentFilmDetails.putExtra("position", position)
                startActivityForResult(intentFilmDetails, FILM_DETAILS_REQUEST_CODE)
            }

            override fun onSaveToFavorites(position: Int) {
                val selectedFavoriteMovie = movies[position]
                selectedFavoriteMovie.isFavorite = !selectedFavoriteMovie.isFavorite
                recycler.adapter?.notifyDataSetChanged()
            }

            override fun onDeleteFromFavorites(position: Int) {
                val isFavoriteMovie = movies[position]
                isFavoriteMovie.isFavorite = false
                recycler.adapter?.notifyDataSetChanged()
            }
        })
    }

    private  fun getLayoutManager() : RecyclerView.LayoutManager {
        val currentOrientation = resources.configuration.orientation
        return if (currentOrientation == Configuration.ORIENTATION_PORTRAIT)
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        else GridLayoutManager(this, 2)
    }


    private fun processThemeMode() {
        sharedPreferences = getSharedPreferences(NIGHT_MODE_PREFERENCES, Context.MODE_PRIVATE)
        dayNightModeButton = findViewById(R.id.day_night_mode)
        dayNightModeButton.setOnClickListener(dayNightModeListener)

        checkNightModeIsActivated()
    }

    private fun setShareClickListener() {
        shareButton = findViewById(R.id.share)
        shareButton.setOnClickListener(shareListener)
    }

    private fun setFavoritesClickListener() {
        favoritesButton = findViewById(R.id.favorites)
        favoritesButton.setOnClickListener(favoritesClickListener)
    }
}

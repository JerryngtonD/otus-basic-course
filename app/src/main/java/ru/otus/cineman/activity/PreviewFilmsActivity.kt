package ru.otus.cineman.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import mu.KLogging
import ru.otus.cineman.R
import ru.otus.cineman.model.Movie

class PreviewFilmsActivity : AppCompatActivity() {
    companion object : KLogging() {
        const val FILMS_TITLE_TAG = "film_title"

        const val FILM_DETAILS_REQUEST_CODE = 12345

        // Заголовки при отправке дополнительной информации в/из MovieDetailsActivity
        const val IS_LIKED = "IS_LIKED"
        const val USER_COMMENT = "USER_COMMENT"
        const val FILM_ID = "FILM_ID"

        // Коды восстановления при пересоздании активности
        const val FILMS_STORED = "FILMS_STORED"

        const val NIGHT_MODE_PREFERENCES = "NIGHT_MODE_PREFS"
        const val KEY_IS_NIGHT_MODE = "IS_NIGHT_MODE"
    }

    var isNightMode = false

    lateinit var sharedPreferences: SharedPreferences
    lateinit var shareButton: Button
    lateinit var dayNightModeButton: Button
    lateinit var moreButtons: List<Button>
    lateinit var films: Map<Int, Movie>

    private val clickListener = View.OnClickListener { view ->
        val parentWrapper = view.parent as ViewGroup
        val textView = parentWrapper.findViewWithTag<TextView>(FILMS_TITLE_TAG)
        val currentFilm = films[textView.id]
        setSelectedFilm(currentFilm)

        logger.info { textView.text }

        val intentFilmDetails = Intent(this, MovieDetailsActivity::class.java)
        intentFilmDetails.putExtra("film_id", currentFilm?.id)
        intentFilmDetails.putExtra("image_id", currentFilm?.imageId)
        intentFilmDetails.putExtra("film_description", currentFilm?.descriptionId)
        intentFilmDetails.putExtra("is_liked", currentFilm?.isLiked)
        intentFilmDetails.putExtra("user_comment", currentFilm?.comment)
        startActivityForResult(intentFilmDetails, FILM_DETAILS_REQUEST_CODE)
    }

    private val dayNightModeListener = View.OnClickListener {
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO)
            saveNightModeState(false)
        } else {
            AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES)
            saveNightModeState(true)
        }
        recreate()
    }

    private val shareListener = View.OnClickListener { _ ->
        logger.info { "Share app with your friends" }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.preview_movies)


        sharedPreferences = getSharedPreferences(NIGHT_MODE_PREFERENCES, Context.MODE_PRIVATE)
        dayNightModeButton = findViewById(R.id.day_night_mode)
        dayNightModeButton.setOnClickListener(dayNightModeListener)

        checkNightModeIsActivated()

        films = mapOf(
            R.id.spider_man to Movie(
                id = R.id.spider_man,
                titleId = R.string.spider_man_title,
                imageId = R.drawable.spiderman,
                descriptionId = R.string.spider_man_description
            ),

            R.id.hulk to Movie(
                id = R.id.hulk,
                titleId = R.string.hulk_title,
                imageId = R.drawable.hulk,
                descriptionId = R.string.hulk_description
            ),

            R.id.batman to Movie(
                id = R.id.batman,
                titleId = R.string.batman_title,
                imageId = R.drawable.batman,
                descriptionId = R.string.batman_description
            )
        )

        moreButtons = listOf(
            findViewById(R.id.show_more1),
            findViewById(R.id.show_more2),
            findViewById(R.id.show_more3)
        )

        moreButtons.forEach {
            it.setOnClickListener(clickListener)
        }

        shareButton = findViewById(R.id.share)
        shareButton.setOnClickListener(shareListener)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        val restoredFilms = savedInstanceState.getParcelableArrayList<Movie>(FILMS_STORED)
        films = restoredFilms?.map {
            it.id to it
        }?.toMap() ?: emptyMap()

        val selectedFilm = films.values.filter { it.isSelected }.takeIf { it.isNotEmpty() }?.first()
        setSelectedFilm(selectedFilm)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILM_DETAILS_REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK) {
                return;
            }
            val filmId = data?.getIntExtra(FILM_ID, Int.MAX_VALUE)
            if (filmId == Int.MAX_VALUE) {
                throw InstantiationException()
            }

            val comment = data?.extras?.getString(USER_COMMENT)
            val isLiked = data?.getBooleanExtra(IS_LIKED, false)

            films[filmId]?.isLiked = isLiked ?: false
            films[filmId]?.comment = comment ?: ""
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val filmsToSave: ArrayList<Movie> = ArrayList(films.values)
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

    private fun setSelectedFilm(currentMovie: Movie?) {
        createdDefaultAppearance()
        if (currentMovie != null) {
            currentMovie.isSelected = true
            findViewById<TextView>(currentMovie.id).setTextColor(Color.GREEN)
        }
    }

    private fun createdDefaultAppearance() {
        films.values.forEach {
            it.isSelected = false
            findViewById<TextView>(it.id).setTextColor(Color.RED)
        }
    }

    private fun checkNightModeIsActivated() {
        if(sharedPreferences.getBoolean(KEY_IS_NIGHT_MODE, false)) {
            AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES)
            isNightMode = true
        } else {
            AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO)
            isNightMode = false
        }
    }

    private fun saveNightModeState(isCheckedNightMode: Boolean) {
        sharedPreferences.edit().apply {
            putBoolean(KEY_IS_NIGHT_MODE, isCheckedNightMode)
        }.apply()
    }
}

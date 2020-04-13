package ru.otus.cineman.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import mu.KLogging
import ru.otus.cineman.R
import ru.otus.cineman.model.Film

class PreviewFilmsActivity : AppCompatActivity() {
    companion object : KLogging() {
        const val FILMS_TITLE_TAG = "film_title"

        const val FILM_DETAILS_REQUEST_CODE = 12345

        // Заголовки при отправке дополнительной информации в/из FilmDetailsActivity
        const val IS_LIKED = "IS_LIKED"
        const val USER_COMMENT = "USER_COMMENT"
        const val FILM_ID = "FILM_ID"

        // Коды восстановления при пересоздании активности
        const val FILMS_STORED = "FILMS_STORED"


    }

    lateinit var shareButton: Button
    lateinit var moreButtons: List<Button>
    lateinit var films: Map<Int, Film>

    private val clickListener = View.OnClickListener { view ->
        val parentWrapper = view.parent as ViewGroup
        val textView = parentWrapper.findViewWithTag<TextView>(FILMS_TITLE_TAG)
        val currentFilm = films[textView.id]
        setSelectedFilm(textView, currentFilm)

        logger.info { textView.text }

        val intentFilmDetails = Intent(this, FilmDetailsActivity::class.java)
        intentFilmDetails.putExtra("film_id", currentFilm?.id)
        intentFilmDetails.putExtra("image_id", currentFilm?.imageId)
        intentFilmDetails.putExtra("film_description", currentFilm?.descriptionId)
        intentFilmDetails.putExtra("is_liked", currentFilm?.isLiked)
        intentFilmDetails.putExtra("user_comment", currentFilm?.comment)
        startActivityForResult(intentFilmDetails, FILM_DETAILS_REQUEST_CODE)
    }

    private val shareListener = View.OnClickListener { _ ->
        logger.info { "Share app with your friends" }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.preview_films)

        films = mapOf(
            R.id.spider_man to Film(
                id = R.id.spider_man,
                titleId = R.string.spider_man_title,
                imageId = R.drawable.spiderman,
                descriptionId = R.string.spider_man_description
            ),

            R.id.hulk to Film(
                id = R.id.hulk,
                titleId = R.string.hulk_title,
                imageId = R.drawable.hulk,
                descriptionId = R.string.hulk_description
            ),

            R.id.batman to Film(
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
        val restoredFilms = savedInstanceState.getParcelableArrayList<Film>(FILMS_STORED)
        films = restoredFilms?.map {
            it.id to it
        }?.toMap() ?: emptyMap()


        val selectedFilm = films.values.filter { it.isSelected }.takeIf { it.isNotEmpty() }?.first()
            ?: return

        val chosenTextViewFilm = findViewById<TextView>(selectedFilm.id)
        setSelectedFilm(chosenTextViewFilm, selectedFilm)
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
        val filmsToSave: ArrayList<Film> = ArrayList(films.values)
        outState.putParcelableArrayList(FILMS_STORED, filmsToSave)
    }

    private fun setSelectedFilm(textView: TextView, currentFilm: Film?) {
        films.values.forEach {
            it.isSelected = false
            findViewById<TextView>(it.id).setTextColor(Color.BLACK)
        }
        currentFilm?.isSelected = true
        textView.setTextColor(Color.GREEN)
    }
}

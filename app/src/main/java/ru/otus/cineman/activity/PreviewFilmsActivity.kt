package ru.otus.cineman.activity

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
    companion object: KLogging() {
        const val FILMS_TITLE_TAG = "film_title"
    }

    lateinit var moreButtons: List<Button>
    lateinit var films: Map<Int, Film>

    private val clickListener = View.OnClickListener { view ->
        val parentWrapper = view.parent as ViewGroup
        val textView = parentWrapper.findViewWithTag<TextView>(FILMS_TITLE_TAG)
        films[textView.id]?.isSelected = true
        films.values.forEach{ findViewById<TextView>(it.id).setTextColor(Color.BLACK) }

        textView.setTextColor(Color.GREEN)
        logger.info { textView.text }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.preview_films)

        films = mapOf(
            R.id.spider_man to Film(
                id = R.id.spider_man,
                titleId = R.string.spider_man_title,
                descriptionId = R.string.spider_man_description
            ),

            R.id.hulk to Film(
                id = R.id.hulk,
                titleId = R.string.hulk_title,
                descriptionId = R.string.hulk_description
            ),

            R.id.batman to Film(
                id = R.id.batman,
                titleId = R.string.batman_title,
                descriptionId = R.string.batman_description
            )
        )

        moreButtons = listOf(
            findViewById(R.id.show_more1),
            findViewById(R.id.show_more2),
            findViewById(R.id.show_more3)
        )

        moreButtons.forEach{
            it.setOnClickListener(clickListener)
        }
    }
}

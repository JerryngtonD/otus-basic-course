package ru.otus.cineman

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import mu.KLogging

class PreviewFilmsActivity : AppCompatActivity() {
    companion object: KLogging()

    lateinit var moreButtons: List<Button>
    lateinit var filmTitles: List<TextView>

    val clickListener = View.OnClickListener {view ->
        val parentWrapper = view.parent as ViewGroup
        val textView = parentWrapper.findViewWithTag<TextView>("film_title")
        filmTitles.forEach{ it.setTextColor(Color.BLACK)}

        textView.setTextColor(Color.GREEN)
        logger.info { textView.text }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.preview_films)

        moreButtons = listOf(
            findViewById(R.id.show_more1),
            findViewById(R.id.show_more2),
            findViewById(R.id.show_more3)
        )

        filmTitles = listOf(
            findViewById(R.id.spider_man),
            findViewById(R.id.hulk),
            findViewById(R.id.batman)
        )

        moreButtons.forEach{
            it.setOnClickListener(clickListener)
        }

        filmTitles.forEach{
            it.setTextColor(Color.BLACK)
        }
    }
}

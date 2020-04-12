package ru.otus.cineman

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import mu.KLogging

class PreviewFilmsActivity : AppCompatActivity() {
    companion object: KLogging()

    val clickListener = View.OnClickListener {view ->
        val parentWrapper = view.parent as ViewGroup
        logger.info { parentWrapper.findViewWithTag<TextView>("film_title").text }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.preview_films)
        val moreButtons = listOf<Button>(
            findViewById(R.id.show_more1),
            findViewById(R.id.show_more2),
            findViewById(R.id.show_more3)
        ).forEach{it.setOnClickListener(clickListener)}
    }
}

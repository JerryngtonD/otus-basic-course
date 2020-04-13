package ru.otus.cineman.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.otus.cineman.R

class FilmDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.film_details)
    }
}

package ru.otus.cineman

import android.app.Application
import ru.otus.cineman.model.MovieItem

class MovieStorage : Application() {
    companion object {
        private lateinit var movies: MutableList<MovieItem>

        fun getMovieStorage() = movies
    }

    override fun onCreate() {
        super.onCreate()
        movies = initializeMovies()
    }

    private fun initializeMovies(): MutableList<MovieItem> {
        return mutableListOf(
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
                titleId = R.string.mummy_title,
                imageId = R.drawable.mummy,
                descriptionId = R.string.mummy_description
            ),
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
}
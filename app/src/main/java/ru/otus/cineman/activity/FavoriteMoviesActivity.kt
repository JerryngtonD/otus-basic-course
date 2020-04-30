//package ru.otus.cineman.activity
//
//import android.content.res.Configuration
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.MenuItem
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.GridLayoutManager
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import mu.KLogging
//import ru.otus.cineman.R
//import ru.otus.cineman.activity.PreviewMoviesActivity.Companion.FAVORITE_MOVIES
//import ru.otus.cineman.adapter.FavoriteMovieAdapter
//import ru.otus.cineman.model.MovieItem
//
//class FavoriteMoviesActivity : AppCompatActivity() {
//    companion object : KLogging()
//
//    lateinit var movies: ArrayList<MovieItem>
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.favorite_movies)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//
//        movies = intent.getParcelableArrayListExtra(FAVORITE_MOVIES)
//        initRecycler()
//    }
//
//    private fun initRecycler() {
//        val recycler = findViewById<RecyclerView>(R.id.favoriteRecyclerView)
//        val layoutManager = getLayoutManager()
//        recycler.layoutManager = layoutManager
//
//        recycler.adapter = FavoriteMovieAdapter(LayoutInflater.from(this), movies)
//    }
//
//    private fun getLayoutManager(): RecyclerView.LayoutManager {
//        val currentOrientation = resources.configuration.orientation
//        return if (currentOrientation == Configuration.ORIENTATION_PORTRAIT)
//            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//        else GridLayoutManager(this, 2)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        finish()
//        return super.onOptionsItemSelected(item)
//    }
//}
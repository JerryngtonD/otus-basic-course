package ru.otus.cineman.activity

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.otus.cineman.MovieStorage
import ru.otus.cineman.MovieStorage.Companion.getFavoriteMovieStorage
import ru.otus.cineman.R
import ru.otus.cineman.fragment.*
import ru.otus.cineman.model.MovieItem
import ru.otus.cineman.model.json.MovieModel
import ru.otus.cineman.model.json.MoviesResult


class MainActivity : AppCompatActivity(), MovieListListener, MovieDetailsListener,
    NavigationView.OnNavigationItemSelectedListener {
    companion object {
        const val TAG = "MovieListFragment"

        // Just for check values from details fragment to pass into movies list fragment for update
        const val IS_PREVIEW_MOVIES_UPDATED_BY_DETAILS = "IS_PREVIEW_MOVIES_UPDATED_BY_DETAILS"
        const val UPDATED_COMMENT = "UPDATED_COMMENT"
        const val UPDATED_IS_LIKED_STATUS = "UPDATED_IS_LIKED_STATUS"
    }

    private var drawer: DrawerLayout? = null
    private var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        if (savedInstanceState == null) {
            openMoviesListFragment()
        }

        drawer = findViewById(R.id.drawer_layout)
        toolbar = findViewById(R.id.toolbar)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer?.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun openMovieDetailsFragment(movieItem: MovieItem) {
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragmentContainer,
                MovieDetailsFragment.newInstance(movieItem),
                MovieDetailsFragment.TAG
            )
            .addToBackStack(null)
            .commit()
    }

    private fun openMoviesListFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, MoviesListFragment(), TAG)
            .commit()
    }

    override fun onMoreClick(movieItem: MovieItem) {
        openMovieDetailsFragment(movieItem)
    }

    override fun onCloseMovieDetails(comment: String?, isLikedStatus: Boolean?) {
        supportFragmentManager.popBackStack()

        val movieListFragment = supportFragmentManager.findFragmentByTag(TAG)
        if (movieListFragment != null) {
            movieListFragment.arguments = Bundle().apply {
                putBoolean(IS_PREVIEW_MOVIES_UPDATED_BY_DETAILS, true)
                putBoolean(UPDATED_IS_LIKED_STATUS, isLikedStatus ?: false)
                putString(UPDATED_COMMENT, comment)
            }
            supportFragmentManager.beginTransaction()
                .show(movieListFragment)
                .commit()
        }
    }

    override fun openFavoriteMovies() {
        if (getFavoriteMovieStorage().size != 0) {
            supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.fragmentContainer, MoviesListFavoriteFragment(), null
                )
                .addToBackStack(null)
                .commit()
        } else {
            Toast.makeText(this, R.string.favorites_empty, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        if (drawer?.isDrawerOpen(GravityCompat.START) == true) {
            drawer?.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_favorites -> openFavoriteMovies()

            else -> Toast.makeText(this, R.string.share, Toast.LENGTH_SHORT).show()
        }
        drawer?.closeDrawer(GravityCompat.START)
        return true
    }
}


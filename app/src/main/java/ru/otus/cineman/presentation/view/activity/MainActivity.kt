package ru.otus.cineman.presentation.view.activity


import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import dagger.android.support.DaggerAppCompatActivity
import ru.otus.cineman.ApplicationParams.MOVIE_KEY
import ru.otus.cineman.R
import ru.otus.cineman.data.entity.FavoriteMovieModel
import ru.otus.cineman.data.entity.MovieModel
import ru.otus.cineman.data.entity.WatchLaterMovieModel
import ru.otus.cineman.di.modules.ViewModelFactory
import ru.otus.cineman.presentation.view.fragment.*
import ru.otus.cineman.presentation.viewmodel.MovieListViewModel
import ru.otus.cineman.presentation.viewmodel.NavigationDrawerViewModel
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
     MovieListListener, OnCloseFragmentListener
{
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    companion object {
        const val TAG = "MOVIES_LIST"
        const val PERMISSION_REQUEST_CODE = 1
    }

    private lateinit var drawer: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navigationView: NavigationView

    private val navigationDrawerViewModel: NavigationDrawerViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(NavigationDrawerViewModel::class.java)
    }
    private val moviesListViewModel: MovieListViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(MovieListViewModel::class.java)
    }

    private var watchLaterMoviesList = mutableListOf<WatchLaterMovieModel>()
    private var favoritesMoviesList = mutableListOf<FavoriteMovieModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))

        drawer = findViewById(R.id.drawer_layout)
        toolbar = findViewById(R.id.toolbar)

        checkNightModeActivated()

        moviesListViewModel.watchLaterMovies.observe(this, Observer {
            watchLaterMovies ->

            watchLaterMoviesList.clear()
            watchLaterMoviesList.addAll(watchLaterMovies)
        })

        moviesListViewModel.favoriteMovies.observe(this, Observer {
            favorites ->

            favoritesMoviesList.clear()
            favoritesMoviesList.addAll(favorites)
        })


        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        launchFirstFragment(savedInstanceState)
    }


    private fun launchFirstFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.fragmentContainer, MoviesListFragment(),
                    TAG
                )
                .addToBackStack("home")
                .commit()

            val movieId = intent.extras?.getString(MOVIE_KEY)
            if (!movieId.isNullOrBlank()) {
                moviesListViewModel.getDataFromPush(movieId)
                onDetailsClick()
            }

            val movie = intent.getParcelableExtra<MovieModel>("movie")
            if(movie != null) {
                moviesListViewModel.onMovieSelect(movie)
                onDetailsClick()
            }
        }
    }

    private fun openWatchLaterFragment() {
        if (!watchLaterMoviesList.isNullOrEmpty()) {
            supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.fragmentContainer, WatchLaterFragment(),
                    WatchLaterFragment.TAG
                )
                .addToBackStack(null)
                .commit()
        } else {
            Toast.makeText(this, R.string.watch_later_empty, Toast.LENGTH_SHORT).show()
        }
    }

    private fun openAboutFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragmentContainer, AboutFragment(),
                WatchLaterFragment.TAG
            )
            .addToBackStack(null)
            .commit()
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_favorites -> onFavoritesClick()
            R.id.day_night_mode -> onDayNightModeChanged()
            R.id.nav_watch_later -> openWatchLaterFragment()
            R.id.about -> openAboutFragment()
            else -> Toast.makeText(this, R.string.share, Toast.LENGTH_SHORT).show()
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }


    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCloseFragment() {
        supportFragmentManager.popBackStack()
    }

    override fun onDetailsClick() {
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragmentContainer,
                MovieDetailsFragment(),
                MovieDetailsFragment.TAG
            )
            .addToBackStack(null)
            .commit()
    }

    override fun onBackPressedByMoviesList() {
        finish()
    }

    private fun onDayNightModeChanged() {
        navigationDrawerViewModel.onDayNightModeChanged()
        recreate()
    }

    private fun checkNightModeActivated() {
        navigationDrawerViewModel.checkNightModeActivated()
    }

    private fun onFavoritesClick() {
        if (!favoritesMoviesList.isNullOrEmpty()) {
            supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.fragmentContainer,
                    MoviesListFavoriteFragment(),
                    MoviesListFavoriteFragment.TAG
                )
                .addToBackStack(null)
                .commit()
        } else {
            Toast.makeText(this, R.string.favorites_empty, Toast.LENGTH_SHORT).show()
        }
    }
}

interface OnCloseFragmentListener {
    fun onCloseFragment()
}


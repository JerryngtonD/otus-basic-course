package ru.otus.cineman.presentation.view.activity


import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import ru.otus.cineman.ApplicationParams.MOVIE_KEY
import ru.otus.cineman.R
import ru.otus.cineman.data.entity.MovieModel
import ru.otus.cineman.presentation.view.fragment.*
import ru.otus.cineman.presentation.viewmodel.MovieListViewModel
import ru.otus.cineman.presentation.viewmodel.NavigationDrawerViewModel
import ru.otus.cineman.presentation.viewmodel.ViewModelFactory

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    MovieDetailsListener, MovieListListener, WatchLaterListener
{
    companion object {
        const val TAG = "MOVIES_LIST"
    }

    private lateinit var drawer: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navigationView: NavigationView

    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var navigationDrawerViewModel: NavigationDrawerViewModel
    private lateinit var moviesListViewModel: MovieListViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))

        drawer = findViewById(R.id.drawer_layout)
        toolbar = findViewById(R.id.toolbar)

        viewModelFactory = ViewModelFactory(context = applicationContext)
        navigationDrawerViewModel =
            ViewModelProvider(this, viewModelFactory).get(NavigationDrawerViewModel::class.java)
        moviesListViewModel =
            ViewModelProvider(this, viewModelFactory).get(MovieListViewModel::class.java)

        checkNightModeActivated()


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
        if (!moviesListViewModel.watchLaterMovies.value.isNullOrEmpty()) {
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_favorites -> onFavoritesClick()
            R.id.day_night_mode -> onDayNightModeChanged()
            R.id.nav_watch_later -> openWatchLaterFragment()
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

    override fun onCloseMovieDetails() {
        supportFragmentManager.popBackStack()
    }

    override fun onCloseWatchLater() {
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
        if (!moviesListViewModel.favoriteMovies.value.isNullOrEmpty()) {
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


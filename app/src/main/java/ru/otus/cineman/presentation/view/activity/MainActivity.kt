package ru.otus.cineman.presentation.view.activity


import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import ru.otus.cineman.R
import ru.otus.cineman.presentation.view.fragment.*
import ru.otus.cineman.presentation.viewmodel.MovieListViewModel
import ru.otus.cineman.presentation.viewmodel.NavigationDrawerViewModel
import ru.otus.cineman.presentation.viewmodel.ViewModelFactory

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    MovieDetailsListener, MovieListListener {
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

        if (savedInstanceState == null) {
            openMoviesListFragment()
        }
    }

    private fun openMoviesListFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragmentContainer, MoviesListFragment(),
                TAG
            )
            .commit()
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_favorites -> onFavoritesClick()
            R.id.day_night_mode -> onDayNightModeChanged()
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


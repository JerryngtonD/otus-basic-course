package ru.otus.cineman.presentation.view.activity


import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import ru.otus.cineman.R
import ru.otus.cineman.data.entity.json.MovieModel
import ru.otus.cineman.presentation.view.fragment.MovieDetailsFragment
import ru.otus.cineman.presentation.view.fragment.MovieDetailsListener
import ru.otus.cineman.presentation.view.fragment.MovieListListener
import ru.otus.cineman.presentation.view.fragment.MoviesListFragment

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    MovieDetailsListener, MovieListListener {
    companion object {
        const val TAG = "MOVIES_LIST"
    }

    private lateinit var drawer: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))

        drawer = findViewById(R.id.drawer_layout)
        toolbar = findViewById(R.id.toolbar)

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

        openMoviesListFragment()
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
//        when (item.itemId) {
////            R.id.nav_favorites -> openFavoriteMovies()
////
////            R.id.day_night_mode -> processThemeMode()
////
////            else -> Toast.makeText(this, R.string.share, Toast.LENGTH_SHORT).show()
//        }
//        drawer?.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCloseMovieDetails(comment: String?, isLikedStatus: Boolean?) {
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
}
//class MainActivity : AppCompatActivity(), MovieListListener, MovieDetailsListener,
//    NavigationView.OnNavigationItemSelectedListener {
//    companion object {
//        const val TAG = "MovieListFragment"
//
//        // Just for check values from details fragment to pass into movies list fragment for update
//        const val IS_PREVIEW_MOVIES_UPDATED_BY_DETAILS = "IS_PREVIEW_MOVIES_UPDATED_BY_DETAILS"
//        const val UPDATED_COMMENT = "UPDATED_COMMENT"
//        const val UPDATED_IS_LIKED_STATUS = "UPDATED_IS_LIKED_STATUS"
//
//
//        const val NIGHT_MODE_PREFERENCES = "NIGHT_MODE_PREFS"
//        const val KEY_IS_NIGHT_MODE = "IS_NIGHT_MODE"
//    }
//
//    private lateinit var sharedPreferences: SharedPreferences
//
//    private var drawer: DrawerLayout? = null
//    private var toolbar: Toolbar? = null
//    var isNightMode = false
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        setSupportActionBar(findViewById(R.id.toolbar))
//        sharedPreferences = getSharedPreferences(NIGHT_MODE_PREFERENCES, Context.MODE_PRIVATE) ?: throw Exception("Can't proceed light mode")
//
//        if (savedInstanceState == null) {
//            openMoviesListFragment()
//        }
//
//        drawer = findViewById(R.id.drawer_layout)
//        toolbar = findViewById(R.id.toolbar)
//
//        val navigationView = findViewById<NavigationView>(R.id.nav_view)
//        navigationView.setNavigationItemSelectedListener(this)
//
//        val toggle = ActionBarDrawerToggle(
//            this,
//            drawer,
//            toolbar,
//            R.string.navigation_drawer_open,
//            R.string.navigation_drawer_close
//        )
//        drawer?.addDrawerListener(toggle)
//        toggle.syncState()
//    }
//

//

//
//    override fun onMoreClick(movieItem: MovieItem) {
//        openMovieDetailsFragment(movieItem)
//    }
//
//    override fun onCloseMovieDetails(comment: String?, isLikedStatus: Boolean?) {
//        supportFragmentManager.popBackStack()
//
//        val movieListFragment = supportFragmentManager.findFragmentByTag(TAG)
//        if (movieListFragment != null) {
//            movieListFragment.arguments = Bundle().apply {
//                putBoolean(IS_PREVIEW_MOVIES_UPDATED_BY_DETAILS, true)
//                putBoolean(UPDATED_IS_LIKED_STATUS, isLikedStatus ?: false)
//                putString(UPDATED_COMMENT, comment)
//            }
//            supportFragmentManager.beginTransaction()
//                .show(movieListFragment)
//                .commit()
//        }
//    }
//
//    override fun openFavoriteMovies() {
//        if (App.instance!!.favoriteMovies.size != 0) {
//            supportFragmentManager
//                .beginTransaction()
//                .replace(
//                    R.id.fragmentContainer, MoviesListFavoriteFragment(), null
//                )
//                .addToBackStack(null)
//                .commit()
//        } else {
//            Toast.makeText(this, R.string.favorites_empty, Toast.LENGTH_SHORT).show()
//        }
//    }
//

//
//    override fun onNavigationItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.nav_favorites -> openFavoriteMovies()
//
//            R.id.day_night_mode -> processThemeMode()
//
//            else -> Toast.makeText(this, R.string.share, Toast.LENGTH_SHORT).show()
//        }
//        drawer?.closeDrawer(GravityCompat.START)
//        return true
//    }
//
//    private fun checkNightModeIsActivated() {
//        isNightMode = sharedPreferences.getBoolean(KEY_IS_NIGHT_MODE, false)
//
//        if (isNightMode) {
//            AppCompatDelegate.setDefaultNightMode(
//                AppCompatDelegate.MODE_NIGHT_NO
//            )
//            saveNightModeState(false)
//        } else {
//            AppCompatDelegate.setDefaultNightMode(
//                AppCompatDelegate.MODE_NIGHT_YES
//            )
//            saveNightModeState(true)
//        }
//        recreate()
//    }
//
//    private fun saveNightModeState(isCheckedNightMode: Boolean) {
//        sharedPreferences.edit().apply {
//            putBoolean(KEY_IS_NIGHT_MODE, isCheckedNightMode)
//        }.apply()
//    }
//
//    private fun processThemeMode() {
//        checkNightModeIsActivated()
//    }
//}


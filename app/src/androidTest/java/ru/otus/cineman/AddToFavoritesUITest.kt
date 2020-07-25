package ru.otus.cineman

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.contrib.DrawerActions.open
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.otus.cineman.presentation.view.activity.MainActivity
import ru.otus.cineman.presentation.view.view_holder.MovieItemViewHolder

@RunWith(AndroidJUnit4::class)
class AddToFavoritesUITest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)


    @Test
    fun testAddToFavoritesClick() {
        onView(withId(R.id.moviesRecycler)).perform(
            RecyclerViewActions.actionOnItemAtPosition<MovieItemViewHolder>(
                0, onViewHolderAction(R.id.isFavorite)
            )
        )

        onView(withId(R.id.drawer_layout)).perform(open());

        onView(withId(R.id.nav_view))
            .perform(NavigationViewActions.navigateTo(R.id.nav_favorites));
    }
}
package ru.otus.cineman

import androidx.lifecycle.ViewModelProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.otus.cineman.presentation.view.activity.MainActivity
import ru.otus.cineman.presentation.viewmodel.MovieListViewModel


@RunWith(AndroidJUnit4ClassRunner::class)
@LargeTest
class GettingErrorWithoutInternetUITest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private var idlingResource: IdlingResource? = null

    @Before
    fun registerIdlingResource() {
        activityRule.scenario.onActivity {
            idlingResource = ViewModelProvider(
                it,
                it.viewModelFactory
            ).get(MovieListViewModel::class.java).idlingResource
        }

        IdlingRegistry.getInstance().register(idlingResource)
    }

    @Test
    fun checkErrorCauseWithoutInternet() {
        onView(withId(R.id.swipeRefresh))
            .perform(swipeDown())

        onView(
            allOf(
                withId(com.google.android.material.R.id.snackbar_text),
                withText(R.string.error_while_loading)
            )
        )
            .check(matches(isDisplayed()));
    }
}
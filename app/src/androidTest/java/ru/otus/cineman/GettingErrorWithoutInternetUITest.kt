package ru.otus.cineman

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.ActivityTestRule
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.otus.cineman.presentation.view.activity.MainActivity


@RunWith(AndroidJUnit4ClassRunner::class)
@LargeTest
class GettingErrorWithoutInternetUITest {
    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun checkErrorCauseWithoutInternet() {
        onView(allOf(withId(com.google.android.material.R.id.snackbar_text),
            withText(R.string.error_while_loading)))
            .check(matches(isDisplayed()));
    }
}
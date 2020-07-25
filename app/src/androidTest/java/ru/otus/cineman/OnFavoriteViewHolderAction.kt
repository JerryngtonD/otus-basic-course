package ru.otus.cineman

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import org.hamcrest.Matcher

fun onViewHolderAction(id: Int) : ViewAction {
    return object : ViewAction {
        override fun getDescription(): String {
            return "On view holder element click action"
        }

        override fun getConstraints(): Matcher<View>? {
            return null
        }

        override fun perform(uiController: UiController?, view: View?) {
            val favoriteButton = view?.findViewById(id) as View
            favoriteButton.performClick()
        }
    }
}
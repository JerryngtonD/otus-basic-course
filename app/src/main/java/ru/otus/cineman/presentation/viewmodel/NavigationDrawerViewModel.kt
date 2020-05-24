package ru.otus.cineman.presentation.viewmodel

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import ru.otus.cineman.presentation.preferences.PreferencesProvider
import ru.otus.cineman.presentation.preferences.PreferencesProvider.Companion.NIGHT_MODE_PREFERENCES

class NavigationDrawerViewModel(
    val context: Context
) : ViewModel() {
    companion object {
        const val KEY_IS_NIGHT_MODE = "IS_NIGHT_MODE"
    }

    private var preferenceProvider = PreferencesProvider(context, NIGHT_MODE_PREFERENCES)

    fun saveNightModeState(isCheckedNightMode: Boolean) {
        preferenceProvider.getPreference().edit().apply {
            putBoolean(KEY_IS_NIGHT_MODE, isCheckedNightMode)
        }.apply()
    }

    fun onDayNightModeChanged() {
        if (!preferenceProvider.getPreference().getBoolean(
                KEY_IS_NIGHT_MODE, false)) {
            AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES
            )
            saveNightModeState(true)
        } else {
            AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO
            )
            saveNightModeState(false)
        }
    }

    fun checkNightModeActivated() {
        if (preferenceProvider.getPreference().getBoolean(KEY_IS_NIGHT_MODE, false)) {
            AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES
            )
        } else {
            AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO
            )
            saveNightModeState(false)
        }
    }
}
package ru.otus.cineman.presentation.viewmodel

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import ru.otus.cineman.presentation.preferences.PreferencesProvider
import ru.otus.cineman.presentation.preferences.PreferencesProvider.Companion.NIGHT_MODE_PREFERENCES
import javax.inject.Inject

class NavigationDrawerViewModel @Inject constructor(
    val application: Application
) : ViewModel() {
    companion object {
        const val KEY_IS_NIGHT_MODE = "IS_NIGHT_MODE"
    }

    private var preferenceProvider = PreferencesProvider(application, NIGHT_MODE_PREFERENCES)

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
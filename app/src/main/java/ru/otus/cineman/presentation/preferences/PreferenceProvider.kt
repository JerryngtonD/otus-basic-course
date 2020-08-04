package ru.otus.cineman.presentation.preferences

import android.content.Context
import android.content.SharedPreferences

class PreferencesProvider (
    val context: Context,
    val preferencesType: String
) {
    companion object {
        const val NIGHT_MODE_PREFERENCES = "NIGHT_MODE_PREFS"
        const val CACHE_LOADING = "CACHE_LOADING"
    }

    private val preference: SharedPreferences = getPreferencesByType(preferencesType)!!

    fun getPreference(): SharedPreferences {
        return preference
    }

    fun getPreferencesByType(preferencesType: String): SharedPreferences? {
        return when(preferencesType) {
            NIGHT_MODE_PREFERENCES -> context.getSharedPreferences(NIGHT_MODE_PREFERENCES, Context.MODE_PRIVATE)
            CACHE_LOADING -> context.getSharedPreferences(CACHE_LOADING, Context.MODE_PRIVATE)
            else -> throw Exception("Unknown preferences type")
        }
    }
}
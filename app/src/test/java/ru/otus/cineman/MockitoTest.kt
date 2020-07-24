package ru.otus.cineman

import android.content.Context
import android.content.SharedPreferences
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations.initMocks
import ru.otus.cineman.presentation.preferences.PreferencesProvider
import ru.otus.cineman.presentation.preferences.PreferencesProvider.Companion.NIGHT_MODE_PREFERENCES

@RunWith(AndroidJUnit4::class)
class MockitoTest {
    @Mock
    lateinit var context: Context

    @Mock
    lateinit var sharedPref: SharedPreferences

    @Before
    fun setUp() {
        initMocks(this)
    }


    @Test
    fun testGetPreferenceByType() {
        Mockito.`when`(context.getSharedPreferences(NIGHT_MODE_PREFERENCES, Context.MODE_PRIVATE)).thenReturn(sharedPref)
        val preferencesProvider = PreferencesProvider(
            context,
            NIGHT_MODE_PREFERENCES
        )
        preferencesProvider.getPreferencesByType(NIGHT_MODE_PREFERENCES)
        Mockito.verify(context.getSharedPreferences(NIGHT_MODE_PREFERENCES, Context.MODE_PRIVATE))
    }
}
package ru.otus.cineman.unit

import android.os.Bundle
import android.os.Parcel
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import ru.otus.cineman.data.entity.MovieModel
import ru.otus.cineman.extension.roundByDigitCount
import ru.otus.cineman.presentation.preferences.PreferencesProvider
import ru.otus.cineman.service.Download
import ru.otus.cineman.service.NotificationWorker


@RunWith(AndroidJUnit4::class)
class UnitTest {
    @Test
    fun testRoundByDigitCount() {
        val beforeTransform = 12.45890
        val afterTransform = beforeTransform.roundByDigitCount(3)
        assertEquals(12.459, afterTransform, 1.0)
    }

    @Test
    fun setDownloadFromParcel() {
        val download = Download(
            progress = 5,
            currentFileSize = 12345.45,
            totalFileSize = 1234566.4
        )

        val parcel = Parcel.obtain()
        download.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)

        val defaultDownload = Download()
        defaultDownload.setDownload(parcel)

        assertEquals(download.progress, defaultDownload.progress)
        assertEquals(download.currentFileSize, defaultDownload.currentFileSize, 1.0)
        assertEquals(download.totalFileSize, defaultDownload.totalFileSize, 1.0)
    }

    @Test
    fun testBuildNotificationIntent() {
        val movieModel = MovieModel(
            movieId = 1234,
            id = 696374,
            title = "qwerty",
            description = "qwertyuiop",
            albumImage = "w2uGvCpMtvRqZg6waC1hvLyZoJa.jpg",
            averageRate = "8.7",
            image = "x6ubMAHjii4dV8oOegzfbqCiYaC.jpg"
        )
        val notificationWorker = NotificationWorker(
            InstrumentationRegistry.getInstrumentation().context, movieModel
        )
        val resultIntent = notificationWorker.buildNotificationIntent(
            movieModel, InstrumentationRegistry.getInstrumentation().context
        )

        val bundle = resultIntent.extras?.get("bundle") as Bundle

        assertEquals(movieModel, bundle["movie"])
    }

    @Test(expected = Exception::class)
    fun testPreferenceProvider() {
        val preferenceProvider = PreferencesProvider(
            InstrumentationRegistry.getInstrumentation().context,
            "unknownType"
        )
        preferenceProvider.getPreference()
    }
}

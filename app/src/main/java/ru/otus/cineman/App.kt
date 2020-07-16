package ru.otus.cineman

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

import ru.otus.cineman.ApplicationParams.CATEGORY_KEY
import ru.otus.cineman.ApplicationParams.MOVIES_PUSH_CHANNEL
import ru.otus.cineman.di.AppComponent
import ru.otus.cineman.di.DaggerAppComponent

class App : DaggerApplication() {
    companion object {
        lateinit var instance: App
            private set

        var TAG = "APP"

        var moviesCategory: String = "popular"
    }

    lateinit var daggerApp: AppComponent

    lateinit var firebaseAnalytics: FirebaseAnalytics
        private set

    lateinit var firebaseRemoteConfig: FirebaseRemoteConfig
        private set



    override fun onCreate() {
        super.onCreate()
        instance = this

        initChannel()

        initFirebase()
    }

    private fun initChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "FilmsSearchApp"
            val description = "Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(MOVIES_PUSH_CHANNEL, name, importance)
            channel.description = description

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun initFirebase() {
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance().apply {
            setDefaultsAsync(R.xml.remote_config)
            fetch(10)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        firebaseRemoteConfig.activate()
                    }
                    moviesCategory = firebaseRemoteConfig.getString(CATEGORY_KEY)
                }
        }

        moviesCategory = firebaseRemoteConfig.getString(CATEGORY_KEY)

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                val token = task.result?.token

                // Log and toast
                Log.d(TAG, token!!)

            })
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        daggerApp =  DaggerAppComponent.builder().application(this).build()
        return daggerApp
    }
}
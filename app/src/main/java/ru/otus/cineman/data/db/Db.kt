package ru.otus.cineman.data.db

import android.content.Context
import androidx.room.Room

object Db {

    private lateinit var INSTANCE: MovieDb

    fun getInstance(context: Context): MovieDb {
        if (!this::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context,
                MovieDb::class.java, "movieDb.db"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
        return INSTANCE
    }

    fun getInMemoryDbInstance(context: Context): MovieDb {
        if (!this::INSTANCE.isInitialized) {
            INSTANCE = Room.inMemoryDatabaseBuilder(
                context,
                MovieDb::class.java
            )
                .fallbackToDestructiveMigration()
                .build()
        }
        return INSTANCE
    }
}
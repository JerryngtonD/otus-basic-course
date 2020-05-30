package ru.otus.cineman.data.db

import android.content.Context
import androidx.room.Room

object Db {

    private var INSTANCE: MovieDb? = null

    fun getInstance(context: Context): MovieDb? {
        if (INSTANCE == null) {
            synchronized(MovieDb::class) {

                INSTANCE = Room.databaseBuilder(
                    context,
                    MovieDb::class.java, "movieDb.db"
                )
                    /*.allowMainThreadQueries()*/
                    .fallbackToDestructiveMigration()
//                    .addMigrations(MIGRATION_1_2)
//                    .addCallback(DbCallback(context))
                    .build()
            }
        }
        return INSTANCE
    }

    fun destroyInstance() {
        INSTANCE?.close()
        INSTANCE = null
    }
}
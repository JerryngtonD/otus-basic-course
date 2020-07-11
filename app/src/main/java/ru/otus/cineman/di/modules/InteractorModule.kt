package ru.otus.cineman.di.modules

import android.app.Application
import dagger.Module
import dagger.Provides
import ru.otus.cineman.data.MovieStorage
import ru.otus.cineman.data.api.MovieService
import ru.otus.cineman.data.db.*
import ru.otus.cineman.domain.MovieInteractor
import ru.otus.cineman.domain.MovieRepository
import javax.inject.Singleton

@Module
class InteractorModule {
    @Singleton
    @Provides
    fun provideDatabase(application: Application) = Db.getInstance(application)

    @Singleton
    @Provides
    fun provideMovieDao(db: MovieDb) = db.getMovieDao()

    @Singleton
    @Provides
    fun provideFavoriteMovieDao(db: MovieDb) = db.getFavoriteMovieDao()

    @Singleton
    @Provides
    fun provideWatchLaterMovieDao(db: MovieDb) = db.getWatchLaterMovieDao()

    @Singleton
    @Provides
    fun provideMovieStorage(
        movieDao: MovieDao,
        favoriteMovieDao: FavoriteMovieDao,
        watchLaterMovieDao: WatchLaterMovieDao
    ) = MovieStorage(
        movieDao = movieDao,
        favoriteMovieDao = favoriteMovieDao,
        watchLaterMovieDao = watchLaterMovieDao
    )

    @Singleton
    @Provides
    fun provideMovieRepository(
        movieStorage: MovieStorage
    ) = MovieRepository(
        storage = movieStorage
    )

    @Singleton
    @Provides
    fun provideInteractor(
        movieService: MovieService,
        movieRepository: MovieRepository
    ) = MovieInteractor(
        movieService = movieService,
        movieRepository = movieRepository
    )
}
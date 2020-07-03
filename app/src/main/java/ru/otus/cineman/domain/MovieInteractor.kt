package ru.otus.cineman.domain

import io.reactivex.Completable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import ru.otus.cineman.App.Companion.moviesCategory
import ru.otus.cineman.data.api.MovieService
import ru.otus.cineman.data.entity.MovieModel
import ru.otus.cineman.presentation.viewmodel.MovieListViewModel.Companion.INIT_PAGE

class MovieInteractor(
    private val movieService: MovieService,
    val movieRepository: MovieRepository
) {
    fun loadInitOrRefresh(isNeedRefresh: Boolean, callback: GetMoviesCallback) {
        movieService.run {
            getPopularMovies(getDefaultMovieCategory(moviesCategory), INIT_PAGE)
                .subscribeOn(Schedulers.computation())
                .subscribeBy(
                    onSuccess = {
                        saveOrUpdateCache(isNeedRefresh, it.results, callback::onSuccess)
                    },
                    onError = { callback.onError("Error while getting movies: ${it.localizedMessage}") }
                )
        }
    }

    fun loadMore(page: Int, callback: GetMoviesCallback) {
        movieService.run {
            getPopularMovies(getDefaultMovieCategory(moviesCategory), page)
                .subscribeOn(Schedulers.computation())
                .subscribeBy(
                    onSuccess = {
                        enrichCacheMovie(it.results, callback::onSuccess)
                    },
                    onError = {
                        callback.onError("Error while getting movies: ${it.localizedMessage}")
                    }
                )
        }
    }

    fun loadMovie(
        movieId: String,
        callback: GetMovieFromPushCallback
    ) {
        movieService.run {
            getMovie(movieId)
                .subscribeOn(Schedulers.computation())
                .subscribeBy(
                    onSuccess = { callback.onSuccess(it) },
                    onError = { callback.onError("Error while getting movie: ${it.localizedMessage}") }
                )
        }
    }

    private fun saveOrUpdateCache(
        isNeedRefresh: Boolean,
        movies: List<MovieModel>,
        finishAction: () -> Unit
    ) {
        if (isNeedRefresh) {
            Completable.fromRunnable {
                movieRepository.storage.refreshAllMovies(movies, finishAction)
            }.subscribeOn(Schedulers.computation())
                .subscribe()

        } else {
            Completable.fromRunnable {
                movieRepository.storage.addAllToMovies(movies, finishAction)
            }.subscribeOn(Schedulers.computation())
                .subscribe()
        }
    }

    fun searchMoviesByText(
        query: String,
        callback: GetSearchedMovies
    ) {
        movieService.run {
            searchMoviesByText(query, page = 1)
                .subscribeOn(Schedulers.computation())
                .subscribeBy(
                    onSuccess = { callback.onSuccess(it.results) },
                    onError = { callback.onError("Error while getting searched movies with query: $query") }
                )
        }
    }

    private fun enrichCacheMovie(movies: List<MovieModel>, finishAction: () -> Unit) {
        Completable.fromRunnable {
            movieRepository.storage.addAllToMovies(movies, finishAction)
        }.subscribeOn(Schedulers.computation())
            .subscribe()
    }

    private fun getDefaultMovieCategory(category: String) =
        if (category.isEmpty()) "popular" else category
}


interface GetMoviesCallback {
    fun onSuccess()
    fun onError(error: String)
}

interface GetMovieFromPushCallback {
    fun onSuccess(movie: MovieModel)
    fun onError(error: String)
}

interface GetSearchedMovies {
    fun onSuccess(movies: List<MovieModel>)
    fun onError(error: String)
}
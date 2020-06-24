package ru.otus.cineman.domain

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.otus.cineman.App.Companion.moviesCategory
import ru.otus.cineman.data.api.MovieService
import ru.otus.cineman.data.entity.MovieModel
import ru.otus.cineman.data.entity.MoviesResult
import ru.otus.cineman.presentation.viewmodel.MovieListViewModel.Companion.INIT_PAGE

class MovieInteractor(
    private val movieService: MovieService,
    val movieRepository: MovieRepository
) {
    fun loadInitOrRefresh(isNeedRefresh: Boolean, callback: GetMoviesCallback) {
        movieService.getPopularMovies(moviesCategory, INIT_PAGE)
            .enqueue(object : Callback<MoviesResult?> {
                override fun onFailure(call: Call<MoviesResult?>, t: Throwable) {
                    callback.onError(t.localizedMessage ?: "")
                }

                override fun onResponse(
                    call: Call<MoviesResult?>,
                    response: Response<MoviesResult?>
                ) {
                    if (response.isSuccessful) {
                        saveOrUpdateCache(
                            isNeedRefresh,
                            response.body()?.results!!,
                            callback::onSuccess
                        )
                    } else {
                        callback.onError("Code: ${response.code()}")
                    }
                }
            })
    }

    fun loadMore(page: Int, callback: GetMoviesCallback) {
        movieService.getPopularMovies(moviesCategory, page)
            .enqueue(object : Callback<MoviesResult?> {
                override fun onFailure(call: Call<MoviesResult?>, t: Throwable) {
                    callback.onError(t.localizedMessage ?: "")
                }

                override fun onResponse(
                    call: Call<MoviesResult?>,
                    response: Response<MoviesResult?>
                ) {
                    if (response.isSuccessful) {
                        enrichCacheMovie(response.body()?.results!!, callback::onSuccess)
                    } else {
                        callback.onError("Code: ${response.code()}")

                    }
                }
            })
    }

    fun loadMovie(
        movieId: String,
        callback: GetMovieFromPushCallback
    ) {
        movieService.getMovie(movieId)
            .enqueue(object : Callback<MovieModel> {
                override fun onResponse(call: Call<MovieModel>, response: Response<MovieModel>) {
                    if (response.isSuccessful) {
                        callback.onSuccess(response.body()!!)
                    } else {
                        callback.onError(response.code().toString() + "")
                    }
                }

                override fun onFailure(call: Call<MovieModel>, t: Throwable) {
                    callback.onError(t.toString())
                }
            })
    }

    private fun saveOrUpdateCache(
        isNeedRefresh: Boolean,
        movies: List<MovieModel>,
        finishAction: () -> Unit
    ) {
        if (isNeedRefresh) {
            movieRepository.storage.refreshAllMovies(movies) {
                finishAction()
            }
        } else {
            movieRepository.storage.addAllToMovies(movies) {
                finishAction()
            }
        }
    }

    private fun enrichCacheMovie(movies: List<MovieModel>, finishAction: () -> Unit) {
        movieRepository.storage.addAllToMovies(movies) {
            finishAction()
        }
    }
}

interface GetMoviesCallback {
    fun onSuccess()
    fun onError(error: String)
}

interface GetMovieFromPushCallback {
    fun onSuccess(movie: MovieModel)
    fun onError(error: String)
}
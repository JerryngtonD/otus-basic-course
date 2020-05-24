package ru.otus.cineman.domain

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.otus.cineman.data.MovieRepository
import ru.otus.cineman.data.MovieService
import ru.otus.cineman.data.entity.json.MovieModel
import ru.otus.cineman.data.entity.json.MoviesResult

class MovieInteractor(
    private val movieService: MovieService,
    private val movieRepository: MovieRepository
) {

    companion object {
        const val INIT_PAGE_NUMBER = 1
    }

    fun getPopularMovies(page: Int, callback: GetMoviesCallback) {
        if (page == INIT_PAGE_NUMBER && movieRepository.getCachedMovies.isNotEmpty()) {
            callback.onSuccess(movieRepository.getCachedMovies)
        }

        movieService.getPopularMovies(page)
            .enqueue(object : Callback<MoviesResult?> {
                override fun onFailure(call: Call<MoviesResult?>, t: Throwable) {
                    callback.onError( t.localizedMessage ?: "")
                }

                override fun onResponse(
                    call: Call<MoviesResult?>,
                    response: Response<MoviesResult?>
                ) {
                    if (response.isSuccessful) {
                        val movies = response.body()!!.results
                        if (page == INIT_PAGE_NUMBER) {
                            movieRepository.addToCache(movies)
                        }
                        callback.onSuccess(movies)
                    } else {
                        callback.onError("""Code: ${response.code()}""")
                    }
                }
            })
    }
}

interface GetMoviesCallback {
    fun onSuccess(movies: List<MovieModel>)
    fun onError(error: String)
}
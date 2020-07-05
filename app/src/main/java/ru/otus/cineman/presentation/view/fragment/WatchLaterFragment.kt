package ru.otus.cineman.presentation.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_watch_later.*
import ru.otus.cineman.service.NotificationCallback
import ru.otus.cineman.service.NotificationWorker
import ru.otus.cineman.R
import ru.otus.cineman.data.entity.WatchLaterMovieModel
import ru.otus.cineman.data.mapper.MoviesMapper
import ru.otus.cineman.presentation.view.adapter.OnItemClickListener
import ru.otus.cineman.presentation.view.adapter.WatchLaterMovieAdapter
import ru.otus.cineman.presentation.viewmodel.MovieListViewModel

class WatchLaterFragment: Fragment() {
    companion object {
      const val TAG = "WATCH_LATER_MOVIES_TAG"
    }

    private lateinit var listener: WatchLaterListener
    private lateinit var watchLaterFragmentAdapter: WatchLaterMovieAdapter
    private val viewModel: MovieListViewModel by lazy {
        ViewModelProvider(requireActivity()).get(MovieListViewModel::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity is WatchLaterListener) {
            listener = activity as WatchLaterListener
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_watch_later, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()

        viewModel.watchLaterMovies.observe(viewLifecycleOwner, Observer { watchLaterMovies ->
            watchLaterFragmentAdapter.setItems(
                watchLaterMovies
            )
        })

        val callback = object : OnBackPressedCallback(
            true
            /** true means that the callback is enabled */
        ) {
            override fun handleOnBackPressed() {
                listener.onCloseWatchLater()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun initRecyclerView() {
        recyclerview_watch_later_films.apply {
            layoutManager = LinearLayoutManager(requireContext())

            watchLaterFragmentAdapter = WatchLaterMovieAdapter(ItemClicks())
            adapter = watchLaterFragmentAdapter
        }
    }

    inner class ItemClicks : OnItemClickListener {

        override fun addClick(watchLaterMovie: WatchLaterMovieModel, itemPosition: Int) {
            val movie = MoviesMapper.mapWatchLaterToMovie(watchLaterMovie)
            NotificationWorker(
                requireContext(),
                movie
            ).notificationSet(object:
                NotificationCallback {
                override fun onSuccess(timeOfNotification: Long) {
                    movie.isWatchLater = true
                    movie.watchTime = timeOfNotification
                    viewModel.addToWatchLater(movie)
                }

                override fun onFailure() {

                }

            })
        }

        override fun removeClick(watchLaterMovie: WatchLaterMovieModel, itemPosition: Int) {
            NotificationWorker(
                requireContext(),
                MoviesMapper.mapWatchLaterToMovie(watchLaterMovie)
            ).cancelNotification()
            viewModel.removeFromWatchLater(watchLaterMovie.id)

        }
    }
}

interface WatchLaterListener {
    fun onCloseWatchLater()
}
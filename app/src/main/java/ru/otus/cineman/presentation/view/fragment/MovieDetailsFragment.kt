package ru.otus.cineman.presentation.view.fragment

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import ru.otus.cineman.ApplicationParams.IMAGE_URL
import ru.otus.cineman.R
import ru.otus.cineman.data.entity.MovieModel
import ru.otus.cineman.presentation.view.activity.MainActivity.Companion.PERMISSION_REQUEST_CODE
import ru.otus.cineman.presentation.view.activity.OnCloseFragmentListener
import ru.otus.cineman.presentation.viewmodel.MovieListViewModel
import ru.otus.cineman.service.NotificationCallback
import ru.otus.cineman.service.NotificationWorker


class MovieDetailsFragment : Fragment() {
    companion object {
        const val TAG = "MovieDetailsFragment"
    }

    private lateinit var movie: MovieModel

    lateinit var listener: OnCloseFragmentListener
    lateinit var movieImage: ImageView
    lateinit var movieTitle: MaterialToolbar
    lateinit var movieDescription: TextView
    lateinit var movieUserComment: EditText
    lateinit var isLikedStatusMovie: CheckBox
    lateinit var watchLater: ImageView
    lateinit var loadImage: ImageView
    lateinit var loadImageProgressBar: ProgressBar
    private lateinit var coordinatorLayout: View

    private val viewModel: MovieListViewModel by lazy {
        ViewModelProvider(requireActivity()).get(MovieListViewModel::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        if (activity is OnCloseFragmentListener) {
            listener = activity as OnCloseFragmentListener
        }
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movie_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        movieImage = view.findViewById(R.id.film_poster)
        movieTitle = view.findViewById(R.id.toolbar_movie_title)
        movieDescription = view.findViewById(R.id.film_details_description)
        movieUserComment = view.findViewById(R.id.user_comment)
        isLikedStatusMovie = view.findViewById(R.id.checked_like)
        watchLater = view.findViewById(R.id.watch_later)
        loadImage = view.findViewById(R.id.load_image)
        loadImageProgressBar = view.findViewById(R.id.progress_bar_image_loader)
        coordinatorLayout = requireActivity().findViewById(R.id.coordinatorMovies)


        viewModel.selectedMovie.observe(viewLifecycleOwner, Observer { selectedMovie ->

            if (selectedMovie != null) {
                movie = selectedMovie

                movieTitle.title = selectedMovie.title
                movieDescription.text = selectedMovie.description
                movieUserComment.setText(selectedMovie.comment)
                isLikedStatusMovie.isChecked = selectedMovie.isLiked

                if (movie.isWatchLater) {
                    watchLater.setImageDrawable(requireActivity().getDrawable(R.drawable.watch_later_on_set))
                }

                Glide.with(movieImage.context)
                    .load("${IMAGE_URL}w500${selectedMovie.albumImage}")
                    .placeholder(R.drawable.ic_loading)
                    .centerCrop()
                    .error(R.drawable.ic_error)
                    .into(movieImage)
            }

        })


        val callback = object : OnBackPressedCallback(
            true
            /** true means that the callback is enabled */
        ) {
            override fun handleOnBackPressed() {
                val selectedMovie = viewModel.selectedMovie.value!!
                val isSelectedMovieNeedUpdate =
                    selectedMovie.isLiked != isLikedStatusMovie.isChecked
                            || selectedMovie.comment != movieUserComment.text.toString()

                if (isSelectedMovieNeedUpdate) {
                    selectedMovie.apply {
                        comment = movieUserComment.text.toString()
                        isLiked = isLikedStatusMovie.isChecked
                    }.let {
                        viewModel.onUpdateSelectedMovieInDetails(it)
                    }
                }
                listener.onCloseFragment()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        initLoadImageListener()
        initWatchLaterListener()
    }

    private fun initLoadImageListener() {
        loadImage.setOnClickListener {
            requireActivity().runOnUiThread {
                loadImageProgressBar.isGone = false
            }

            if (checkPermission()) {
                downloadImage()
            } else {
                requestPermission()
            }
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun initWatchLaterListener() {
        watchLater.setOnClickListener {

            if (movie.isWatchLater) {
                movie.isWatchLater = false

                viewModel.removeFromWatchLater(movie.id)

                NotificationWorker(
                    requireContext(),
                    movie
                ).cancelNotification()

                watchLater.setImageDrawable(requireActivity().getDrawable(R.drawable.watch_later_off))

            } else {
                NotificationWorker(
                    requireContext(),
                    movie
                ).notificationSet(object :
                    NotificationCallback {
                    override fun onSuccess(timeOfNotification: Long) {
                        movie.isWatchLater = true
                        movie.watchTime = timeOfNotification
                        viewModel.addToWatchLater(movie)
                        watchLater.setImageDrawable(requireActivity().getDrawable(R.drawable.watch_later_on_set))
                    }

                    override fun onFailure() {
                        TODO("Not yet implemented")
                    }

                })
            }
        }
    }

    private fun downloadImage() {
        Glide.with(requireContext())
            .asBitmap()
            .load(IMAGE_URL + "original" + movie.albumImage)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadFailed(errorDrawable: Drawable?) {
                    requireActivity().runOnUiThread {
                        Snackbar.make(
                            coordinatorLayout,
                            R.string.retry,
                            Snackbar.LENGTH_LONG
                        ).setAction(R.string.retry) {
                            downloadImage()
                        }.show()
                        loadImageProgressBar.isGone = true
                    }

                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val contentValues = ContentValues().apply {
                            put(MediaStore.Images.Media.TITLE, movie.title)
                            put(
                                MediaStore.Images.Media.DISPLAY_NAME,
                                movie.title
                            )
                            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                            put(
                                MediaStore.Images.Media.DATE_ADDED,
                                System.currentTimeMillis() / 1000
                            )
                            put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Cineman")
                        }

                        val contentResolver = requireActivity().contentResolver
                        val uri = contentResolver.insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            contentValues
                        )
                        val outputStream = contentResolver.openOutputStream(uri!!)

                        resource.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                        outputStream?.close()
                    } else {
                        MediaStore.Images.Media.insertImage(
                            requireActivity().contentResolver,
                            resource,
                            movie.title,
                            movie.description
                        )
                    }

                    requireActivity().runOnUiThread {
                        Snackbar.make(
                            coordinatorLayout,
                            R.string.success_loaded_image,
                            Snackbar.LENGTH_LONG
                        )
                            .setAction(R.string.downloaded_image) {
                                val intent = Intent()
                                intent.action = Intent.ACTION_VIEW
                                intent.type = "image/*"
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            }.show()

                        loadImageProgressBar.isGone = true
                    }
                }
            })
    }
}
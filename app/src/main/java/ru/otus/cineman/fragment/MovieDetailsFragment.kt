package ru.otus.cineman.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import ru.otus.cineman.R
import ru.otus.cineman.model.MovieItem
import java.lang.Exception

class MovieDetailsFragment : Fragment() {
    companion object {
        const val TAG = "MovieDetailsFragment"


        const val MOVIE_IMAGE = "MOVIE_IMAGE"
        const val MOVIE_DESCRIPTION = "MOVIE_DESCRIPTION"
        const val IS_LIKED = "IS_LIKED"
        const val MOVIE_COMMENT = "MOVIE_COMMENT"


        fun newInstance(movieItem: MovieItem): MovieDetailsFragment {
            return MovieDetailsFragment().apply {
                arguments = Bundle().apply {
                    putInt(MOVIE_IMAGE, movieItem.imageId)
                    putInt(MOVIE_DESCRIPTION, movieItem.descriptionId)
                    putBoolean(IS_LIKED, movieItem.isLiked)
                    putString(MOVIE_COMMENT, movieItem.comment)
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        if (activity is MovieDetailsListener) {
            listener = activity as MovieDetailsListener
        }
        super.onActivityCreated(savedInstanceState)
    }

    var listener: MovieDetailsListener? = null

    var movieImage: ImageView? = null
    var movieDescription: TextView? = null
    var movieUserComment: EditText? = null
    var isLikedStatusMovie: CheckBox? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movie_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        movieImage =  view.findViewById<ImageView>(R.id.film_poster)
        movieDescription = view.findViewById<TextView>(R.id.film_details_description)
        movieUserComment = view.findViewById<EditText>(R.id.user_comment)
        isLikedStatusMovie = view.findViewById<CheckBox>(R.id.checked_like)

        movieImage?.setImageResource(arguments?.getInt(MOVIE_IMAGE) ?: throw Exception("Image id should be presented"))
        movieDescription?.setText(arguments?.getInt(MOVIE_DESCRIPTION) ?: throw Exception("Description value should be presented"))
        movieUserComment?.setText(arguments?.getString(MOVIE_COMMENT))
        isLikedStatusMovie?.isChecked = arguments?.getBoolean(IS_LIKED) ?: throw Exception("Like status should be presented")

        val callback = object : OnBackPressedCallback(true /** true means that the callback is enabled */) {
            override fun handleOnBackPressed() {
                listener?.onCloseMovieDetails(movieUserComment?.text.toString(), isLikedStatus = isLikedStatusMovie?.isChecked)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }
}

interface MovieDetailsListener {
    fun onCloseMovieDetails(comment: String?, isLikedStatus: Boolean?)
}
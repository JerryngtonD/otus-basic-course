package ru.otus.cineman.presentation.view.view_holder

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ru.otus.cineman.R
import ru.otus.cineman.data.entity.WatchLaterMovieModel
import ru.otus.cineman.ApplicationParams.IMAGE_URL
import java.text.SimpleDateFormat
import java.util.*

class WatchLaterMovieViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val movieTitle: TextView = itemView.findViewById(R.id.watch_later_title)
    private val movieImage: ImageView = itemView.findViewById(R.id.watch_later_image)
    private val timeOfNotification: TextView = itemView.findViewById(R.id.watch_later_time_text)
    private val clockIcon: ImageView = itemView.findViewById(R.id.watch_later_clock_icon)
    private val button: Button = itemView.findViewById(R.id.cancel_button)

    fun bind(movie: WatchLaterMovieModel) {
        movieTitle.text = movie.title
        val formatter = SimpleDateFormat("HH:mm | dd.MM.yyyy", Locale.getDefault())
        val notificationTimeInMillis = movie.timeOfNotification

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = notificationTimeInMillis

        val time = "${itemView.context.getString(R.string.watch_on)} ${formatter.format(calendar.time)}"
        timeOfNotification.text = time

        if (notificationTimeInMillis < System.currentTimeMillis()) {
            timeOfNotification.setTextColor(itemView.resources.getColor(R.color.design_default_color_error))
            clockIcon.setImageDrawable(itemView.context.getDrawable(R.drawable.watch_later_elapsed))
            button.setText(R.string.dismiss_text)
        } else {
            timeOfNotification.setTextColor(itemView.resources.getColor(R.color.colorPrimary))
            clockIcon.setImageDrawable(itemView.context.getDrawable(R.drawable.watch_later_on_set))
            button.setText(R.string.cancel)
        }

        val requestOptions = RequestOptions()
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)

        Glide.with(itemView.context)
            .applyDefaultRequestOptions(requestOptions)
            .load(IMAGE_URL + "w500" + movie.albumImage)
            .error(R.drawable.ic_error)
            .centerCrop()
            .into(movieImage)
    }
}

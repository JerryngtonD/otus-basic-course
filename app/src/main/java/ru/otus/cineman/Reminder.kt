package ru.otus.cineman

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import ru.otus.cineman.data.entity.MovieModel
import ru.otus.cineman.ApplicationParams.CHANNEL
import ru.otus.cineman.ApplicationParams.IMAGE_URL
import ru.otus.cineman.presentation.view.activity.MainActivity

class Reminder: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val bundle = intent?.getBundleExtra("bundle")
        val movie = bundle?.get("movie") as MovieModel

        val movieTitle = movie.title
        val notificationId = movie.id

        val mIntent = Intent(context, MainActivity::class.java)
        mIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        mIntent.putExtra("movie", movie)

        val pendingIntent = PendingIntent.getActivity(context, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(context!!, CHANNEL).apply {
            setSmallIcon(R.drawable.light_mode)
            setContentTitle(context.getString(R.string.app_name))
            setContentText("${context.getString(R.string.watch_later_notification)} $movieTitle")
            priority = NotificationCompat.PRIORITY_LOW
            setContentIntent(pendingIntent)
            setAutoCancel(true)
        }


        val notificationManager = NotificationManagerCompat.from(context)

        Glide.with(context)
            .asBitmap()
            .load(IMAGE_URL + "w780" + movie.albumImage)
            .into(object: CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    builder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(resource))
                    notificationManager.notify(notificationId, builder.build())
                }
            })

        notificationManager.notify(notificationId, builder.build())
    }
}
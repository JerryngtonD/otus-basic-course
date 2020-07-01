package ru.otus.cineman.service

import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ru.otus.cineman.ApplicationParams.CHANNEL
import ru.otus.cineman.ApplicationParams.IMAGE_URL
import ru.otus.cineman.ApplicationParams.MOVIE_KEY
import ru.otus.cineman.R
import ru.otus.cineman.presentation.view.activity.MainActivity

class CustomFirebaseMessagingService: FirebaseMessagingService() {
    override fun onNewToken(p0: String) {
        print(21)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val remoteMessageData = remoteMessage.data

        if (remoteMessageData.isNotEmpty()) {
            notify(remoteMessage)
        }
    }


    private fun notify(remoteMessage: RemoteMessage) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(MOVIE_KEY, remoteMessage.data["movie"])
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val builder = NotificationCompat.Builder(this, CHANNEL).apply {
            setSmallIcon(R.drawable.firebase_icon)
            setContentTitle(remoteMessage.data["title"])
            setContentText(remoteMessage.data["text"])
            priority = NotificationCompat.PRIORITY_DEFAULT
            setContentIntent(pendingIntent)
            setAutoCancel(true)
        }

        val notificationManager = NotificationManagerCompat.from(this)
        val notificationId = System.currentTimeMillis().toInt()

        Glide.with(this)
            .asBitmap()
            .load(IMAGE_URL + "w500" + remoteMessage.data["image"])
            .into(object : CustomTarget<Bitmap>() {
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
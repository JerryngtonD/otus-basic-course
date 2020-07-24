package ru.otus.cineman.service

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import ru.otus.cineman.data.entity.MovieModel
import java.util.*

class NotificationWorker(private val context: Context, val movieModel: MovieModel) {
    private var dateTimeInMillis = 0L

    private fun makeIntent() {
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = buildNotificationIntent(movieModel, context)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            dateTimeInMillis,
            pendingIntent
        )
    }

    fun cancelNotification() {
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(movieModel.id.toString(), null, context, Reminder::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        alarmManager.cancel(
            pendingIntent
        )
    }

    fun buildNotificationIntent(movieModel: MovieModel, context: Context): Intent {
        val intent = Intent(movieModel.id.toString(), null, context, Reminder::class.java)

        val bundle = Bundle()
        bundle.putParcelable("movie", movieModel)
        intent.putExtra("bundle", bundle)
        return intent
    }


    fun notificationSet(callback: NotificationCallback) {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        DatePickerDialog(
            context,
            DatePickerDialog.OnDateSetListener { _, dpdYear, dpdMonth, dayOfMonth ->
                val timeSetListener =
                    TimePickerDialog.OnTimeSetListener { _, hourOfDay, pickerMinute ->
                        val pickedDateTime = Calendar.getInstance()
                        pickedDateTime.set(dpdYear, dpdMonth, dayOfMonth, hourOfDay, pickerMinute, 0)
                        dateTimeInMillis = pickedDateTime.timeInMillis

                        movieModel.isWatchLater = true
                        movieModel.watchTime = dateTimeInMillis

                        makeIntent()
                        callback.onSuccess(dateTimeInMillis)
                    }

                TimePickerDialog(
                    context,
                    timeSetListener,
                    currentHour,
                    currentMinute,
                    true
                ).show()

            },
            currentYear,
            currentMonth,
            currentDay
        ).show()
    }
}

interface NotificationCallback {
    fun onSuccess(timeOfNotification: Long)
    fun onFailure()
}
package ru.otus.cineman.service

import android.app.IntentService
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import ru.otus.cineman.ApplicationParams
import ru.otus.cineman.ApplicationParams.IMAGE_URL
import ru.otus.cineman.ApplicationParams.MESSAGE_LOADER_INTENT
import ru.otus.cineman.R
import ru.otus.cineman.data.api.ImageService
import ru.otus.cineman.extension.roundByDigitCount
import java.io.*
import java.util.*


private const val IMAGE_LOADER_SERVICE_NAME = "IMAGE_LOADER"

class ImageLoader : IntentService(IMAGE_LOADER_SERVICE_NAME) {
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private lateinit var notificationManager: NotificationManager
    private var totalFileSize: Double = 0.0


    override fun onHandleIntent(intent: Intent?) {
        val url = intent?.extras?.getString("image_url") ?: ""
        val movieName = intent?.extras?.getString("movie_name") ?: ""

        if (url.isNotEmpty()) {
            notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


            val notificationId = Random().nextInt(10000)
            notificationBuilder =
                NotificationCompat.Builder(this, ApplicationParams.MOVIES_PUSH_CHANNEL)
                    .setSmallIcon(R.drawable.image_loader)
                    .setContentTitle("Download")
                    .setContentText("Downloading Image")
                    .setAutoCancel(true)
            notificationManager.notify(notificationId, notificationBuilder.build())

            downloadImage(url, movieName, notificationId)
        }
    }


    private fun downloadImage(movieUrl: String, movieName: String, notificationId: Int) {
        val retrofit = OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                val url = chain.request()
                    .url()
                    .newBuilder()
                    .addQueryParameter("api_key", ApplicationParams.API_KEY)
                    .build()

                val request = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()
                return@Interceptor chain.proceed(request)
            })
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
            .let { okHttpClient ->
                Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(IMAGE_URL)
                    .client(OkHttpClient.Builder().build())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build()
            }

        val downloadService = retrofit.create(ImageService::class.java)

        val subscribe = downloadService.downloadImage("original/$movieUrl")
            .subscribeOn(Schedulers.newThread())
            .doOnSuccess { response ->
                writeResponseBodyToDisk(
                    response.body()!!,
                    movieName,
                    notificationId
                )
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = {
                    Toast.makeText(
                        this,
                        "Some error while loading image on url: $movieUrl",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )

    }

    fun writeResponseBodyToDisk(body: ResponseBody, movieName: String, notificationId: Int) {
        var count: Int
        val data = ByteArray(1024 * 4)
        val fileSize = body.contentLength()
        val bis: InputStream = BufferedInputStream(body.byteStream(), 1024 * 8)
        val outputFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "$movieName.jpg"
        )
        val output: OutputStream = FileOutputStream(outputFile)
        var total: Long = 0
        val startTime = System.currentTimeMillis()
        var timeCount = 1
        while (bis.read(data).also { count = it } != -1) {
            total += count.toLong()
            //Кол-во мегабайт, которое занимает файл
            totalFileSize = (fileSize / Math.pow(1024.0, 2.0)).roundByDigitCount(4)
            //Сколько мегабайт я загрузил сейчас
            val current = (total / Math.pow(1024.0, 2.0)).roundByDigitCount(4)
            //Процентная часть загруженного ко всему размеру файла (байты)
            val progress = (total * 100 / fileSize).toInt()
            //Прошедшее время от начала предыдущего отсчета
            val currentTime = System.currentTimeMillis() - startTime
            val download = Download()
            download.totalFileSize = totalFileSize
            if (currentTime > 1000 * timeCount) {
                download.currentFileSize = current
                download.progress = progress
                sendNotification(download, notificationId)
                timeCount++
            }
            output.write(data, 0, count)
        }
        onDownloadComplete(notificationId)
        output.flush()
        output.close()
        bis.close()
    }

    private fun sendNotification(download: Download, notificationId: Int) {
        sendIntent(download)
        notificationBuilder.setProgress(100, download.progress, false)
        notificationBuilder.setContentText("Downloading file " + download.currentFileSize + "/" + totalFileSize + " MB")
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private fun sendIntent(download: Download) {
        val intent = Intent(MESSAGE_LOADER_INTENT)
        intent.putExtra("download", download)
        LocalBroadcastManager.getInstance(this@ImageLoader).sendBroadcast(intent)
    }

    private fun onDownloadComplete(notificationId: Int) {
        val download = Download()
        download.progress = 100
        sendIntent(download)
        notificationManager.cancel(0)
        notificationBuilder.setProgress(0, 0, false)
        notificationBuilder.setContentText("File Downloaded")
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}
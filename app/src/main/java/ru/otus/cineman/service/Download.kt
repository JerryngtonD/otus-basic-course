package ru.otus.cineman.service

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Download(
    var progress: Int = 0,
    var currentFileSize: Double = 0.0,
    var totalFileSize: Double = 0.0
): Parcelable {
    private fun setDownload(inObject: Parcel) {
        progress = inObject.readInt()
        currentFileSize = inObject.readDouble()
        totalFileSize = inObject.readDouble()
    }
}
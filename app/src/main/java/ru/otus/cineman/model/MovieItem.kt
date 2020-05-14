package ru.otus.cineman.model
import android.os.Parcel
import android.os.Parcelable

data class MovieItem(
    var title: String? = "",
    var image: String? = "",
    var description: String? = "",
    var isSelected: Boolean = false,
    var isLiked: Boolean = false,
    var comment: String? = "",
    var isFavorite: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(out: Parcel?, id: Int) {
        out?.writeString(title)
        out?.writeString(image)
        out?.writeString(description)
        out?.writeByte((if (isSelected) 0 else 1).toByte())
        out?.writeByte((if (isLiked) 0 else 1).toByte())
        out?.writeString(comment)
        out?.writeByte((if (isSelected) 0 else 1).toByte())
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<MovieItem> {
        override fun createFromParcel(parcel: Parcel): MovieItem {
            return MovieItem(parcel)
        }

        override fun newArray(size: Int): Array<MovieItem?> {
            return arrayOfNulls(size)
        }
    }
}

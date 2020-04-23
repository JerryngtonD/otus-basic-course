package ru.otus.cineman.model
import android.os.Parcel
import android.os.Parcelable

data class Movie(
    var id: Int,
    var titleId: Int,
    var imageId: Int,
    var descriptionId: Int,
    var isSelected: Boolean = false,
    var isLiked: Boolean = false,
    var comment: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readString()
    )

    override fun writeToParcel(out: Parcel?, id: Int) {
        out?.writeInt(this.id)
        out?.writeInt(titleId)
        out?.writeInt(imageId)
        out?.writeInt(descriptionId)
        out?.writeByte((if (isSelected) 0 else 1).toByte())
        out?.writeByte((if (isLiked) 0 else 1).toByte())
        out?.writeString(comment)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Movie> {
        override fun createFromParcel(parcel: Parcel): Movie {
            return Movie(parcel)
        }

        override fun newArray(size: Int): Array<Movie?> {
            return arrayOfNulls(size)
        }
    }
}

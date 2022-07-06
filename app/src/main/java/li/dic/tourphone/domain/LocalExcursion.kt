package li.dic.tourphone.domain

import android.os.Parcel
import android.os.Parcelable
import li.dic.tourphone.domain.auth.Excursion
import java.text.SimpleDateFormat
import java.util.*

data class LocalExcursion(
    var id: Int,
    var timeStart: String,
    var timeEnd: String,
    var name: String,
    var listeners: Int = 0,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(timeStart)
        parcel.writeString(timeEnd)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun fromApiExcursion(excursion: Excursion, locale: Locale) {
        val mainDateFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", locale)
        val hourFormat = SimpleDateFormat("HH:mm", locale)
        val date: Date? = mainDateFormat.parse(excursion.start.trim())

        id = excursion.id
        timeStart = hourFormat.format(date)
        timeEnd = "11:00"
        name = excursion.name
    }

    companion object CREATOR : Parcelable.Creator<LocalExcursion> {
        override fun createFromParcel(parcel: Parcel): LocalExcursion {
            return LocalExcursion(parcel)
        }

        override fun newArray(size: Int): Array<LocalExcursion?> {
            return arrayOfNulls(size)
        }
    }
}
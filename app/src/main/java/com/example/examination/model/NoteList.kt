import android.os.Parcel
import android.os.Parcelable

data class NoteList(
    val title: String,
    val note: String,
    val date: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(note)
        parcel.writeString(date)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NoteList> {
        override fun createFromParcel(parcel: Parcel): NoteList {
            return NoteList(parcel)
        }

        override fun newArray(size: Int): Array<NoteList?> {
            return arrayOfNulls(size)
        }
    }
}


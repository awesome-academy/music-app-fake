package com.example.zingmp3phake.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SongInfo(
    var songid: String? = null,
    var songName: String = "",
    var songArtist: String = "",
    var duration: Int = 0,
    var songUrl: String = "",
    var songImg: String = "",
) : Parcelable

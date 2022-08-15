package com.example.zingmp3phake.data.model

import android.os.Parcelable
import com.example.zingmp3phake.utils.N0_LYRIC
import kotlinx.parcelize.Parcelize

@Parcelize
class Song(
    var songInfo : SongInfo = SongInfo(),
    var isLocal: Boolean = false,
    var isFavorite: Boolean = false,
    var lyrics: MutableList<String> = mutableListOf()
) : Parcelable {

    companion object {
        const val SONG_ID = "songid"
        const val SONG_NAME = "songname"
        const val SONG_ARTIST = "songartist"
        const val SONG_DURATION = "songduration"
        const val SONG_URL = "songurl"
        const val SONG_IMG = "songimg"
        const val SONG_LOCAL = "songlocal"
        const val SONG_FAVORITE = "songfavorite"
        const val SONG_LYRIC = "songlyric"
    }
}

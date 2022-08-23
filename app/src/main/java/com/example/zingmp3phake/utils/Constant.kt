package com.example.zingmp3phake.utils

import android.os.Handler
import android.os.Looper
import java.util.*
import java.util.concurrent.TimeUnit

object Constant {
    const val READ_PERMISSION_REQUEST_CODE = 101
    const val ACTION_MUSIC = "action.music"
    const val ACTION_MUSIC_BROADCAST = "action.music.broadcast"
    const val CHANNEL_ID = "chanel_id_notification"
    const val CHANNEL_NAME = "Music"
    const val NOTIFICATION_ID = 1
    const val TITILE_PRE = "previous"
    const val TITILE_NEXT = "next"
    const val TITILE_PLAY = "play"
    const val TITILE_PAUSE = "pause"
    const val TITLE_LOCAL = "Danh sách bài hát trên thiết bị"
    const val TITLE_FAVORITE = "Danh sách bài hát yêu thích"
    const val BUNDLE_TITLE_STRING_KEY = "title"
    const val BUNDLE_LIST_KEY = "list"
    const val BUNDLE_SONG_KEY = "song"
    const val DATA_KEY = "data"
    const val SONG = " Bài hát"
    const val TAG_MEDIA_SESSION = "tag"
    const val MEDIA_EXTERNAL_AUDIO_URI = "content://media/external/audio/albumart"
    const val TAG_LOG = "AAAAAA"
    const val N0_LYRIC = "Chưa có lời cho bài hát này"
    const val NO_DATA = "No Data Founded"
    const val NO_INTERNET = "Không có kết nối mạng"
    const val PAGE_SIZE = 10
    const val UTF = "utf-8"
}

val handler = Handler(Looper.getMainLooper())

fun getTimetoMiliSecond(second: Int): String {
    val minute = TimeUnit.MILLISECONDS.toMinutes(second.toLong())
    val second = TimeUnit.MILLISECONDS.toSeconds(second.toLong()) % MINUTE_SECOND
    val time = String.format(Locale.US, "%02d:%02d", minute, second)
    return time
}

fun getTimetoSecond(second: Int): String {
    val minute = TimeUnit.SECONDS.toMinutes(second.toLong())
    val second = TimeUnit.SECONDS.toSeconds(second.toLong()) % MINUTE_SECOND
    val time = String.format(Locale.US, "%02d:%02d", minute, second)
    return time
}

enum class MusicAction {
    START, NEXT, PERVIOUS, PLAYORPAUSE, FAVORITE
}

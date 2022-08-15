package com.example.zingmp3phake.utils

import android.os.Handler
import android.os.Looper
import java.util.*
import java.util.concurrent.TimeUnit

const val TIME_DELAY_LAUNCH = 2000L
const val DB_NAME = "song.db"
const val DB_VERSION = 3
const val TABLE_SONG = "tblsong"
const val DROP_TABLE = "DROP TABLE IF EXISTS tblsong"
const val CREATE_TABLE =
    "CREATE TABLE IF NOT EXISTS tblsong (songid varchar(255) primary key, songname varchar(255), " +
            "songartist varchar(255),songduration int, songurl varchar(255), songimg varchar(255), " +
            "songlocal int, songfavorite int,songlyric varchar(255) )"
const val READ_PERMISSION_REQUEST_CODE = 101
const val TIME_SLEEP = 500L
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
const val DATA_KEY = "data"
const val SONG = " Bài hát"
const val TAG_MEDIA_SESSION = "tag"
const val MEDIA_EXTERNAL_AUDIO_URI = "content://media/external/audio/albumart"
const val NEXT_SONG = "action.next.song"
const val PREVIOUS_SONG = "action.previous.song"
const val API_TRENDING_ZING_SONG =
    "https://mp3.zing.vn/xhr/chart-realtime?songId=0&videoId=0&albumId=0&chart=song&time=-1"
const val API_ZING_SONG = "https://mp3.zing.vn/xhr/media/get-source?type=audio&key="
const val API_BASE_SPOTIFY = " "
const val PLAY_OR_PAUSE_SONG = "action.play.or.pause.song"
const val ZING_SONG_ID = "code"
const val ZING_SONG_NAME = "title"
const val ZING_SONG_ARTIST = "artists_names"
const val ZING_SONG_DURATION = "duration"
const val ZING_SONG_IMG = "thumbnail"
const val ZING_SONG_SOURCE = "source"
const val ZING_SONG_URL = "128"
const val ZING_SONG = "song"
const val ZING_DATA = "data"
const val METHOD_GET = "GET"
const val TAG_LOG = "AAAAAA"
const val N0_LYRIC = "Chưa có lời cho bài hát này"
const val NO_DATA = "No Data Founded"
const val NO_INTERNET = "Không có kết nối mạng"
val handler = Handler(Looper.getMainLooper())
const val MINUTE_SECOND = 60
const val INDEX_0 = 0
const val INDEX_1 = 1
const val INDEX_2 = 2
const val INDEX_3 = 3
const val INDEX_4 = 4
const val INDEX_5 = 5
const val INDEX_6 = 6
const val INDEX_7 = 7
const val INDEX_8 = 8

fun getTimetoSecond(second: Int): String {
    val minute = TimeUnit.MILLISECONDS.toMinutes(second.toLong())
    val second = TimeUnit.MILLISECONDS.toSeconds(second.toLong()) % MINUTE_SECOND
    val time = String.format(Locale.US,"%02d:%02d", minute, second)
    return time
}

enum class MusicAction {
    START, NEXT, PERVIOUS, PLAYORPAUSE, FAVORITE
}

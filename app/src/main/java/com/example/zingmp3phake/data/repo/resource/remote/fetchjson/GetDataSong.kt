package com.example.zingmp3phake.data.repo.resource.remote.fetchjson

import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.utils.API_ZING_SONG
import com.example.zingmp3phake.utils.MILISECOND_OF_SECOND
import com.example.zingmp3phake.utils.N0_LYRIC
import com.example.zingmp3phake.utils.SPOTIFY_LINE
import com.example.zingmp3phake.utils.SPOTIFY_LYRIC
import com.example.zingmp3phake.utils.SPOTIFY_WORD
import com.example.zingmp3phake.utils.ZING_DATA
import com.example.zingmp3phake.utils.ZING_SONG_ALBUM
import com.example.zingmp3phake.utils.ZING_SONG_ARTIST
import com.example.zingmp3phake.utils.ZING_SONG_DURATION
import com.example.zingmp3phake.utils.ZING_SONG_ID
import com.example.zingmp3phake.utils.ZING_SONG_IMG
import com.example.zingmp3phake.utils.ZING_SONG_NAME
import com.example.zingmp3phake.utils.ZING_SONG_SOURCE
import com.example.zingmp3phake.utils.ZING_SONG_URL
import org.json.JSONException
import org.json.JSONObject
import java.net.URL
import java.util.logging.Logger

class GetDataSong {

    fun getInZingAPI(id: String?): Song {
        val url = URL(API_ZING_SONG + id)
        val result = GetJson().getJsonFromZINGAPI(url)
        val jsonObject = JSONObject(result)
        val dataObject = jsonObject.getJSONObject(ZING_DATA)
        return Song().apply {
            dataObject.let {
                songInfo.songid = it.getString(ZING_SONG_ID)
                songInfo.songName = it.getString(ZING_SONG_NAME)
                songInfo.duration = it.getInt(ZING_SONG_DURATION) * MILISECOND_OF_SECOND
                try {
                    val albumObject = it.getJSONObject(ZING_SONG_ALBUM)
                    songInfo.songImg = albumObject.getString(ZING_SONG_IMG)
                } catch (e: JSONException) {
                    Logger.getLogger(e.toString())
                    songInfo.songImg = it.getString(ZING_SONG_IMG)
                }
                songInfo.songArtist = it.getString(ZING_SONG_ARTIST)
                val sourceObject = it.getJSONObject(ZING_SONG_SOURCE)
                songInfo.songUrl = sourceObject.getString(ZING_SONG_URL)
                lyrics = N0_LYRIC
            }
        }
    }

    fun parseToData(jsonObject: JSONObject, key: String): Any {
        return when (key) {
            SPOTIFY_LYRIC -> parseToLyric(jsonObject)
            else -> {}
        }
    }

    private fun parseToLyric(dataObject: JSONObject): Any {
        val data = mutableListOf<String>()
        val lyricObject = dataObject.getJSONObject(SPOTIFY_LYRIC)
        val arrayLineOject = lyricObject.getJSONArray(SPOTIFY_LINE)
        for (i in 0..arrayLineOject.length() - 1) {
            val lineOject = arrayLineOject.get(i) as JSONObject
            data.add(lineOject.getString(SPOTIFY_WORD))
        }
        return data
    }
}

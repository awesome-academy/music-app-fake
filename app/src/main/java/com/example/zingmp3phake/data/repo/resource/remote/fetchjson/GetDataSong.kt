package com.example.zingmp3phake.data.repo.resource.remote.fetchjson

import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.utils.API_ZING_SONG
import com.example.zingmp3phake.utils.N0_LYRIC
import com.example.zingmp3phake.utils.NO_DATA
import com.example.zingmp3phake.utils.ZING_DATA
import com.example.zingmp3phake.utils.ZING_SONG_ARTIST
import com.example.zingmp3phake.utils.ZING_SONG_DURATION
import com.example.zingmp3phake.utils.ZING_SONG_ID
import com.example.zingmp3phake.utils.ZING_SONG_IMG
import com.example.zingmp3phake.utils.ZING_SONG_NAME
import com.example.zingmp3phake.utils.ZING_SONG_SOURCE
import com.example.zingmp3phake.utils.ZING_SONG_URL
import org.json.JSONObject
import java.net.URL

class GetDataSong {
    fun getInZingAPI(id: String): Song {
        val url = URL(API_ZING_SONG + id)
        val result = GetJson().getJsonFromZINGAPI(url)
        val jsonObject = JSONObject(result)
        val dataObject = jsonObject.getJSONObject(ZING_DATA)
        return Song().apply {
            dataObject.let {
                songInfo.songid = it.getString(ZING_SONG_ID)
                songInfo.songName = it.getString(ZING_SONG_NAME)
                songInfo.duration = it.getInt(ZING_SONG_DURATION)
                songInfo.songImg = it.getString(ZING_SONG_IMG)
                songInfo.songArtist = it.getString(ZING_SONG_ARTIST)
                val sourceObject = it.getJSONObject(ZING_SONG_SOURCE)
                songInfo.songUrl = sourceObject.getString(ZING_SONG_URL)
                lyrics = mutableListOf(N0_LYRIC)
            }
        }
    }
}

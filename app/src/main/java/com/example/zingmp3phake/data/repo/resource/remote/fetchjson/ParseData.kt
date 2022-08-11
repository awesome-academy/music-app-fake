package com.example.zingmp3phake.data.repo.resource.remote.fetchjson

import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.utils.ZING_SONG_ARTIST
import com.example.zingmp3phake.utils.ZING_SONG_DURATION
import com.example.zingmp3phake.utils.ZING_SONG_ID
import com.example.zingmp3phake.utils.ZING_SONG_IMG
import com.example.zingmp3phake.utils.ZING_SONG_NAME
import com.example.zingmp3phake.utils.ZING_SONG_SOURCE
import com.example.zingmp3phake.utils.ZING_SONG_URL
import org.json.JSONObject

class ParseData {
    fun SongDataInZingParseJson(jsonObject: JSONObject) = Song().apply {
        jsonObject.let {
            songid = it.getString(ZING_SONG_ID)
            songName = it.getString(ZING_SONG_NAME)
            duration = it.getInt(ZING_SONG_DURATION)
            songImg = it.getString(ZING_SONG_IMG)
            val sourceObject = it.getJSONObject(ZING_SONG_SOURCE)
            songArtist = it.getString(ZING_SONG_ARTIST)
            songUrl = sourceObject.getString(ZING_SONG_URL)
        }
    }

    fun SongInfoInZingParseJson(jsonObject: JSONObject) = Song().apply {
        jsonObject.let {
            songid = it.getString(ZING_SONG_ID)
            songName = it.getString(ZING_SONG_NAME)
            duration = it.getInt(ZING_SONG_DURATION)
            songImg = it.getString(ZING_SONG_IMG)
            songArtist = it.getString(ZING_SONG_ARTIST)
        }
    }
}

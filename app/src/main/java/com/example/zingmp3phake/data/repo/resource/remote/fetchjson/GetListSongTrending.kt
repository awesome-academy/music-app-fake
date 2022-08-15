package com.example.zingmp3phake.data.repo.resource.remote.fetchjson

import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.data.repo.resource.Listener
import com.example.zingmp3phake.data.repo.resource.local.LocalSong
import com.example.zingmp3phake.utils.API_TRENDING_ZING_SONG
import com.example.zingmp3phake.utils.NO_DATA
import com.example.zingmp3phake.utils.ZING_DATA
import com.example.zingmp3phake.utils.ZING_SONG
import com.example.zingmp3phake.utils.ZING_SONG_ID
import com.example.zingmp3phake.utils.handler
import org.json.JSONObject
import java.net.URL
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class GetListSongTrending(
    private val listener: Listener<MutableList<Song>>
) {
    private val mExecutor: Executor = Executors.newSingleThreadExecutor()

    init {
        mExecutor.execute {
            fetchAPI(listener)
        }
    }

    private fun fetchAPI(listener: Listener<MutableList<Song>>) {
        val url = URL(API_TRENDING_ZING_SONG)
        val jsonResult = GetJson().getJsonFromZINGAPI(url)
        val listSong = mutableListOf<Song>()
        if (jsonResult != null) {
            val resultObject = JSONObject(jsonResult)
            val dataObject = resultObject.getJSONObject(ZING_DATA)
            val arraySongObject = dataObject.getJSONArray(ZING_SONG)
            for (i in 0..arraySongObject.length() - 1) {
                val jsonObject: JSONObject = arraySongObject.get(i) as JSONObject
                val id = jsonObject.getString(ZING_SONG_ID)
                val song = LocalSong.getInstance().getSong(id)
                if (song == null) {
                    listSong.add(GetDataSong().getInZingAPI(id))
                } else listSong.add(song)
            }
            mExecutor.execute {
                handler.post {
                    listener.onSuccess(listSong)
                }
            }
        } else listener.onFail(NO_DATA)
    }
}

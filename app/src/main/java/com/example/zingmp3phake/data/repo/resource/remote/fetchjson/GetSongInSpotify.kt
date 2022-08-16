package com.example.zingmp3phake.data.repo.resource.remote.fetchjson

import com.example.zingmp3phake.data.repo.resource.Listener
import com.example.zingmp3phake.utils.NO_DATA
import com.example.zingmp3phake.utils.handler
import org.json.JSONObject
import java.net.URL
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class GetSongInSpotify<T>(
    private val listener: Listener<T>,
    private val key: String,
    private val url: URL
) {

    private val mExecutor: Executor = Executors.newSingleThreadExecutor()

    init {
        mExecutor.execute {
            fetchAPI()
        }
    }

    private fun fetchAPI() {
        val result = GetJson().getJonFromSpotifyAPI(url)
        if (result != null) {
            val data = GetDataSong().parseToData(JSONObject(result), key) as T
            handler.post {
                if (data == null) listener.onFail(NO_DATA)
                else listener.onSuccess(data)
            }
        }
    }
}

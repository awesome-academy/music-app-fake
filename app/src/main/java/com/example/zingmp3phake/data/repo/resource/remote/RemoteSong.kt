package com.example.zingmp3phake.data.repo.resource.remote

import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.data.repo.resource.Listener
import com.example.zingmp3phake.data.repo.resource.SongDataSource
import com.example.zingmp3phake.data.repo.resource.remote.fetchjson.GetListSongTrending

class RemoteSong : SongDataSource.SongRemoteSource {

    override fun getTrendingSong(listen: Listener<MutableList<Song>>) {
        GetListSongTrending(listen)
    }

    companion object {
        private var instance: RemoteSong? = null
        fun getInstance() = synchronized(this) {
            instance ?: RemoteSong().also { instance = it }
        }
    }
}

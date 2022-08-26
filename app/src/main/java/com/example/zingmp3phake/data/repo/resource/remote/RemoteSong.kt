package com.example.zingmp3phake.data.repo.resource.remote

import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.data.repo.resource.Listener
import com.example.zingmp3phake.data.repo.resource.SongDataSource
import com.example.zingmp3phake.data.repo.resource.remote.fetchjson.GetListSongTrending
import com.example.zingmp3phake.data.repo.resource.remote.fetchjson.GetSongInSpotify
import com.example.zingmp3phake.utils.ApiConstant
import java.net.URL

class RemoteSong : SongDataSource.SongRemoteSource {

    override fun getTrendingSong(listen: Listener<MutableList<Song>>) {
        GetListSongTrending(listen)
    }

    override fun getLyricSong(url: URL, listen: Listener<MutableList<String>>) {
        GetSongInSpotify(listen, ApiConstant.SPOTIFY_LYRIC, url)
    }

    override fun getResultSearchSong(url: URL, listen: Listener<MutableList<Song>>) {
        GetSongInSpotify(listen, ApiConstant.SPOTIFY_TRACK, url)
    }

    companion object {
        private var instance: RemoteSong? = null
        fun getInstance() = synchronized(this) {
            instance ?: RemoteSong().also { instance = it }
        }
    }
}

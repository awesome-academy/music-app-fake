package com.example.zingmp3phake.data.repo.resource

import android.content.Context
import com.example.zingmp3phake.data.model.Song
import java.net.URL

interface SongDataSource {
    interface SongLocalSource {
        fun getSongLocal(context: Context?, listen: Listener<MutableList<Song>>)
        fun getSongRecent(context: Context?, listen: Listener<MutableList<Song>>)
        fun getSongFavorite(listen: Listener<MutableList<Song>>)
        fun addSongRecent(song: Song)
        fun addSongFavorite(song: Song)
        fun removeSongFavorite(song: Song)
        fun getSong(id: String): Song?
    }

    interface SongRemoteSource {
        fun getTrendingSong(listen: Listener<MutableList<Song>>)
        fun getLyricSong(url: URL, listen: Listener<MutableList<String>>)
        fun getResultSearchSong(url: URL, listen: Listener<MutableList<Song>>)
    }
}

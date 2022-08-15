package com.example.zingmp3phake.data.repo

import android.content.Context
import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.data.repo.resource.Listener
import com.example.zingmp3phake.data.repo.resource.SongDataSource

class SongRepo private constructor(
    val local: SongDataSource.SongLocalSource,
    val remote: SongDataSource.SongRemoteSource
) : SongDataSource.SongLocalSource, SongDataSource.SongRemoteSource {
    override fun getSongLocal(context: Context, listen: Listener<MutableList<Song>>) {
        local.getSongLocal(context, listen)
    }

    override fun getSongRecent(context: Context, listen: Listener<MutableList<Song>>) {
        local.getSongRecent(context, listen)
    }

    override fun getSongFavorite(listen: Listener<MutableList<Song>>) {
        local.getSongFavorite(listen)
    }

    override fun addSongRecent(song: Song) {
        local.addSongRecent(song)
    }

    override fun addSongFavorite(song: Song) {
        local.addSongFavorite(song)
    }

    override fun removeSongFavorite(song: Song) {
        local.removeSongFavorite(song)
    }

    override fun getSong(id: String): Song? {
        return local.getSong(id)
    }

    override fun getTrendingSong(listen: Listener<MutableList<Song>>) {
        remote.getTrendingSong(listen)
    }

    companion object {
        private var instance: SongRepo? = null
        fun getInstance(
            local: SongDataSource.SongLocalSource,
            remote: SongDataSource.SongRemoteSource
        ) =
            synchronized(this) {
                instance ?: SongRepo(local, remote).also { instance = it }
            }
    }
}

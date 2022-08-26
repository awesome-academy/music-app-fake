package com.example.zingmp3phake.screen.detailplaylist

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.screen.MusicService

class DetailPlaylistPresenter : DetailPlaylistContract.Presenter {

    private var view: DetailPlaylistContract.View? = null
    private var isConnected = false
    private lateinit var musicService: MusicService
    private var serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.LocalBinder
            musicService = binder.getService()
            isConnected = true
            getCurrentSong()
            view?.displayPlayOrPause(musicService.isPlayings)
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isConnected = false
        }
    }

    override fun getCurrentSong() {
        if (musicService.listSongs.size == 0) {
            view?.displayCurrentSong(Song())
        } else {
            val song = musicService.listSongs.get(musicService.positions)
            view?.displayCurrentSong(song)
        }
    }

    override fun bindService(context: Context) {
        val intent = Intent(context, MusicService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun unBindService(context: Context) {
        if (isConnected) context.unbindService(serviceConnection)
    }

    override fun handleFavorite() {
        view?.displayFavorite(musicService.listSongs.get(musicService.positions).isFavorite)
    }

    override fun handlePlayOrPauseSong() {
        view?.displayPlayOrPause(musicService.isPlayings)
    }

    override fun handleStartSong(list: MutableList<Song>, pos: Int) {
        musicService.startSong(list, pos)
    }

    override fun onStart() {
        // TODO("Not yet implemented")
    }

    override fun onStop() {
        // TODO("Not yet implemented")
    }

    override fun setView(view: DetailPlaylistContract.View?) {
        this.view = view
    }
}

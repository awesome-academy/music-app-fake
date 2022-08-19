package com.example.zingmp3phake.screen.detailplaylist

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.screen.MusicService
import com.example.zingmp3phake.utils.ACTION_MUSIC
import com.example.zingmp3phake.utils.MusicAction
import com.example.zingmp3phake.utils.TIME_DELAY_FOR_LOAD
import com.example.zingmp3phake.utils.handler

class DetailPlaylistPresenter(
    private val view: DetailPlaylistContract.View
) : DetailPlaylistContract.Presenter {

    private var isConnected = false
    private lateinit var musicService: MusicService
    private var serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.LocalBinder
            musicService = binder.getService()
            isConnected = true
            getCurrentSong()
            view.displayPlayOrPause(musicService.isPlayings)
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isConnected = false
        }
    }
    var localReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            when (intent?.getStringExtra(ACTION_MUSIC)) {
                MusicAction.START.name -> {
                    handler.removeCallbacksAndMessages(null)
                    getCurrentSong()
                }
                MusicAction.PLAYORPAUSE.name -> {
                    handler.removeCallbacksAndMessages(null)
                    handler.postDelayed({
                        view.displayPlayOrPause(musicService.isPlayings)
                    }, TIME_DELAY_FOR_LOAD)
                }
                MusicAction.NEXT.name -> {
                    handler.removeCallbacksAndMessages(null)
                    handler.postDelayed({
                        getCurrentSong()
                    }, TIME_DELAY_FOR_LOAD)
                }
                MusicAction.FAVORITE.name -> {
                    handler.removeCallbacksAndMessages(null)
                    handler.postDelayed({
                        handleFavorite()
                    }, TIME_DELAY_FOR_LOAD)
                }
                else -> {}
            }
        }
    }

    override fun getCurrentSong() {
        if (musicService.listSongs.size == 0) {
            view.displayCurrentSong(Song())
        } else {
            val song = musicService.listSongs.get(musicService.positions)
            view.displayCurrentSong(song)
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
        view.displayFavorite(musicService.listSongs.get(musicService.positions).isFavorite)
    }

    override fun handleStartSong(list: MutableList<Song>, pos: Int) {
        musicService.startSong(list, pos)
    }
}

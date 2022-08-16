package com.example.zingmp3phake.screen.mainapp

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.IBinder
import com.example.zingmp3phake.data.repo.SongRepository
import com.example.zingmp3phake.screen.MusicService
import com.example.zingmp3phake.utils.ACTION_MUSIC
import com.example.zingmp3phake.utils.ACTION_MUSIC_BROADCAST
import com.example.zingmp3phake.utils.MusicAction

class MainActivityPresenter(val songRepo: SongRepository, val mainView: MainContract.View) :
    MainContract.Presenter {

    private var context: Context? = null
    private var musicService: MusicService = MusicService()
    private var isConnection = false
    private var serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.LocalBinder
            musicService = binder.getService()
            isConnection = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isConnection = false
        }
    }
    private var localReciver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            when (intent?.getStringExtra(ACTION_MUSIC)) {
                MusicAction.START.name -> handleStartSong()
                MusicAction.NEXT.name -> handleNextSong()
                MusicAction.PLAYORPAUSE.name -> handlePlayOrPauseSong()
                MusicAction.PERVIOUS.name -> handlePreviousSong()
                MusicAction.FAVORITE.name -> handleFavoriteSong()
                else -> {}
            }
        }
    }

    override fun registerBroadcast() {
        val filter = IntentFilter(ACTION_MUSIC_BROADCAST)
        context?.registerReceiver(localReciver, filter)
    }

    override fun unRegisterBroadcast() {
        context?.unregisterReceiver(localReciver)
    }

    override fun bindService(context: Context) {
        if (isConnection == false) {
            this.context = context
            val intent = Intent(context.applicationContext, MusicService::class.java)
            this.context?.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun unBindService() {
        if (isConnection) {
            context?.unbindService(serviceConnection)
            isConnection = false
        }
    }

    override fun handleNextSong() {
        val postion = (musicService.positions + 1) % musicService.listSongs.size
        musicService.startSong(musicService.listSongs, postion)
    }

    override fun handlePlayOrPauseSong() {
        if (musicService.isPlayings) mainView.onPauseSong()
        else mainView.onPlaySong()
        musicService.playOrPause()
    }

    override fun handlePreviousSong() {
        val position =
            if (musicService.positions - 1 >= 0) musicService.positions - 1
            else musicService.listSongs.size - 1
        musicService.startSong(musicService.listSongs, position)
    }

    override fun handleStartSong() {
        val song = musicService.listSongs.get(musicService.positions)
        mainView.onStartSong(song)
    }

    override fun handleFavoriteSong() {
        val song = musicService.listSongs.get(musicService.positions)
        if (song.isFavorite) {
            mainView.displayUnFavorite()
            songRepo.removeSongFavorite(song)
            song.isFavorite = false
        } else {
            mainView.displayFavotite()
            songRepo.addSongFavorite(song)
            song.isFavorite = true
        }
        musicService.listSongs.set(musicService.positions, song)
    }
}

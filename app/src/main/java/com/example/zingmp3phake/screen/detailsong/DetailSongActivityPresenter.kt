package com.example.zingmp3phake.screen.detailsong

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.example.zingmp3phake.data.repo.SongRepository
import com.example.zingmp3phake.data.repo.resource.Listener
import com.example.zingmp3phake.screen.MusicService
import com.example.zingmp3phake.utils.ACTION_MUSIC
import com.example.zingmp3phake.utils.API_BASE_SPOTIFY
import com.example.zingmp3phake.utils.MusicAction
import com.example.zingmp3phake.utils.N0_LYRIC
import com.example.zingmp3phake.utils.TIME_DELAY_FOR_LOAD
import com.example.zingmp3phake.utils.TIME_SLEEP_100
import com.example.zingmp3phake.utils.handler
import java.net.URL

class DetailSongActivityPresenter(
    val detailSongView: DetailSongContract.View,
    val songRepo: SongRepository
) :
    DetailSongContract.Presenter {

    private var isConnected = false
    private lateinit var musicService: MusicService
    private var context: Context? = null
    private var serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.LocalBinder
            musicService = binder.getService()
            isConnected = true
            getCurrentSong()
            getTimeforView()
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isConnected = false
        }
    }
    var localReciver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            when (intent?.getStringExtra(ACTION_MUSIC)) {
                MusicAction.START.name -> {
                    handler.removeCallbacksAndMessages(null)
                    getTimeforView()
                    getCurrentSong()
                }
                MusicAction.PLAYORPAUSE.name -> {
                    handler.removeCallbacksAndMessages(null)
                    handler.postDelayed({
                        handlePlayOrPause()
                    }, TIME_DELAY_FOR_LOAD)
                }
                MusicAction.NEXT.name -> {
                    handler.removeCallbacksAndMessages(null)
                    handler.postDelayed({
                        getCurrentSong()
                        getTimeforView()
                    }, TIME_DELAY_FOR_LOAD)
                }
                MusicAction.PERVIOUS.name -> {
                    handler.removeCallbacksAndMessages(null)
                    handler.postDelayed({
                        getCurrentSong()
                        getTimeforView()
                    }, TIME_DELAY_FOR_LOAD)
                }
                else -> {}
            }
        }
    }

    private fun handlePlayOrPause() {
        detailSongView.displayPlayOrPause(musicService.isPlayings)
    }

    override fun bindService(context: Context) {
        this.context = context
        if (isConnected == false) {
            val service = Intent(context, MusicService::class.java)
            context.bindService(service, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun unBindService() {
        if (isConnected) {
            context?.unbindService(serviceConnection)
        }
    }

    override fun getCurrentSong() {
        if (isConnected == false) {
            context?.let { bindService(it) }
        } else {
            val song = musicService.listSongs.get(musicService.positions)
            detailSongView.displayCurrentSong(song)
        }
    }

    override fun handleChangeSeekBar(value: Int) {
        musicService.onChangeSeekBar(value)
    }

    override fun getLyrics() {
        val song = musicService.listSongs.get(musicService.positions)
        if (song.lyrics.equals(N0_LYRIC)) detailSongView.displayLyricSong(mutableListOf())
        else {
            val url = URL("${API_BASE_SPOTIFY}track_lyrics/?id=${song.songInfo.songid}")
            songRepo.remote.getLyricSong(
                url,
                object : Listener<MutableList<String>> {
                    override fun onSuccess(list: MutableList<String>) {
                        detailSongView.displayLyricSong(list)
                    }

                    override fun onFail(msg: String) {
                        // TODO("Not yet implemented")
                    }
                }
            )
        }
    }

    override fun handleEventFavorite() {
        val song = musicService.listSongs.get(musicService.positions)
        detailSongView.displayFavorite(song.isFavorite)
    }

    private fun getTimeforView() {
        handler.post(object : Runnable {
            override fun run() {
                val time = musicService?.getCurrentSongTime()
                if (time != null) {
                    detailSongView.displayCurrentTimeSong(time)
                } else detailSongView.displayCurrentTimeSong(0)
                handler.removeCallbacks(this)
                handler.postDelayed(this, TIME_SLEEP_100)
            }
        })
    }
}

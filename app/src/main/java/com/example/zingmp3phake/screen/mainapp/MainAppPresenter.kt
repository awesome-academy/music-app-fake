package com.example.zingmp3phake.screen.mainapp

import android.content.Context
import com.example.zingmp3phake.data.repo.SongRepository
import com.example.zingmp3phake.screen.MusicService
import com.example.zingmp3phake.utils.NetworkUtils

class MainAppPresenter(val songRepo: SongRepository) :
    MainContract.Presenter {

    private var mainView: MainContract.View? = null
    var musicService: MusicService = MusicService()

    override fun onStart() {
        // TODO no-op
    }

    override fun onStop() {
        // TODO("Not yet implemented")
    }

    override fun setView(view: MainContract.View?) {
        this.mainView = view
    }

    override fun handleNextSong(context: Context) {
        val postion = (musicService.positions + 1) % musicService.listSongs.size
        val song = musicService.listSongs.get(postion)
        if (song.isLocal == false && NetworkUtils.isNetworkAvailable(context) == false) {
            mainView?.displayNoInternet()
            return
        }
        musicService.startSong(musicService.listSongs, postion)
    }

    override fun handlePlayOrPauseSong(context: Context) {
        if (musicService.isPlayings) mainView?.onPauseSong()
        else mainView?.onPlaySong()
        musicService.playOrPause()
    }

    override fun handlePreviousSong(context: Context) {
        val position =
            if (musicService.positions - 1 >= 0) musicService.positions - 1
            else musicService.listSongs.size - 1
        val song = musicService.listSongs.get(position)
        if (song.isLocal == false && NetworkUtils.isNetworkAvailable(context) == false) {
            mainView?.displayNoInternet()
            return
        }
        musicService.startSong(musicService.listSongs, position)
    }

    override fun handleStartSong(context: Context) {
        val song = musicService.listSongs.get(musicService.positions)
        if (song.isLocal == false && NetworkUtils.isNetworkAvailable(context) == false) {
            mainView?.displayNoInternet()
            return
        }
        mainView?.onStartSong(song)
    }

    override fun handleFavoriteSong(context: Context) {
        val song = musicService.listSongs.get(musicService.positions)
        if (song.isFavorite) {
            mainView?.displayUnFavorite()
            songRepo.removeSongFavorite(song)
            song.isFavorite = false
        } else {
            mainView?.displayFavotite()
            songRepo.addSongFavorite(song)
            song.isFavorite = true
        }
        musicService.listSongs.set(musicService.positions, song)
    }
}

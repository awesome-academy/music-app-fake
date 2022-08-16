package com.example.zingmp3phake.screen.mainapp

import android.content.Context
import com.example.zingmp3phake.data.model.Song

interface MainContract {
    interface View {
        fun onStartSong(song: Song)
        fun onPlaySong()
        fun onPauseSong()
        fun displayFavotite()
        fun displayUnFavorite()
    }

    interface Presenter {
        fun registerBroadcast()
        fun bindService(context: Context)
        fun unBindService()
        fun handleNextSong()
        fun handlePlayOrPauseSong()
        fun handlePreviousSong()
        fun handleStartSong()
        fun handleFavoriteSong()
        fun unRegisterBroadcast()
    }
}

package com.example.zingmp3phake.screen.mainapp

import android.content.Context
import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.utils.base.BasePresenter

interface MainContract {
    interface View {
        fun onStartSong(song: Song)
        fun onPlaySong()
        fun onPauseSong()
        fun displayFavotite()
        fun displayUnFavorite()
        fun displayNoInternet()
    }

    interface Presenter : BasePresenter<View> {
        fun handleNextSong(context: Context)
        fun handlePlayOrPauseSong(context: Context)
        fun handlePreviousSong(context: Context)
        fun handleStartSong(context: Context)
        fun handleFavoriteSong(context: Context)
    }
}

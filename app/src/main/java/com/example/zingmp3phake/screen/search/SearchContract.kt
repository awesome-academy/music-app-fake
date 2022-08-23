package com.example.zingmp3phake.screen.search

import android.content.Context
import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.utils.base.BasePresenter

interface SearchContract {
    interface View {
        fun displayCurrentSong(song: Song)
        fun displayFavorite(isFavorite: Boolean)
        fun displayPlayOrPause(isPlaying: Boolean)
        fun displayResultSearch(songs: MutableList<Song>)
    }

    interface Presenter : BasePresenter<View> {
        fun getCurrentSong()
        fun getFisrtResultSearch(query: String?)
        fun getMoreResultSearch()
        fun handlePlayOrPauseSong()
        fun bindService(context: Context)
        fun handleFavorite()
        fun handleStartSong(song: Song)
    }
}

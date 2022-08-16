package com.example.zingmp3phake.screen.detailsong

import android.content.Context
import com.example.zingmp3phake.data.model.Song

interface DetailSongContract {

    interface View {
        fun displayCurrentSong(song: Song)
        fun displayCurrentTimeSong(time: Int)
        fun displayLyricSong(lyric: MutableList<String>)
        fun displayPlayOrPause(isPlaying: Boolean)
        fun displayFavorite(isFavorite: Boolean)
    }

    interface Presenter {
        fun bindService(context: Context)
        fun unBindService()
        fun getCurrentSong()
        fun handleChangeSeekBar(value: Int)
        fun getLyrics()
        fun handleEventFavorite()
    }
}

package com.example.zingmp3phake.screen.detailplaylist

import android.content.Context
import com.example.zingmp3phake.data.model.Song

interface DetailPlaylistContract {
    interface View {
        fun displayCurrentSong(song: Song)
        fun displayFavorite(isFavorite: Boolean)
        fun displayPlayOrPause(isPlaying: Boolean)
    }

    interface Presenter {
        fun getCurrentSong()
        fun bindService(context: Context)
        fun unBindService(context: Context)
        fun handleFavorite()
        fun handleStartSong(list: MutableList<Song>, pos: Int)
    }
}

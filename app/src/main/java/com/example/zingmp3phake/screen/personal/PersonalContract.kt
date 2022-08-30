package com.example.zingmp3phake.screen.personal

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.utils.base.BasePresenter

interface PersonalContract {
    interface View {
        fun getLocalSongSuccess(list: MutableList<Song>)
        fun getSongFail(msg: String)
        fun getRecentSong(list: MutableList<Song>)
        fun getFavoriteSongSuccess(list: MutableList<Song>)
        fun displayNoInternet()
    }

    interface Presenter : BasePresenter<View> {
        fun getLocalSong(context: AppCompatActivity)
        fun getRecentSong(context: Context?)
        fun getFavoriteSong(context: Context?)
        fun handleStartSong(list: MutableList<Song>, pos: Int, context: Context?)
        fun addSongRecent()
    }
}

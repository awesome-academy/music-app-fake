package com.example.zingmp3phake.presenter

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.zingmp3phake.data.model.Song

interface IPersonalFragment {
    interface View {
        fun getLocalSongSuccess(list: MutableList<Song>)
        fun getLocalSongFail(msg: String)
        fun getRecentSong(list: MutableList<Song>)
    }

    interface Presenter {
        fun getLocalSong(context: AppCompatActivity)
        fun getRecentSong(context: Context)
        fun getFavoriteSong()
        fun handleStartSong(list: MutableList<Song>, pos: Int)
        fun handleClickIntent(title: String)
    }
}

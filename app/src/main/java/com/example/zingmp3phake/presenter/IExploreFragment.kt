package com.example.zingmp3phake.presenter

import android.content.Context
import com.example.zingmp3phake.data.model.Song

interface IExploreFragment {
    interface View {
        fun displaySuccess(list: MutableList<Song>)
        fun displayFail(msg: String)
    }

    interface Presenter {
        fun stopService()
        fun getTrendingSong(context: Context?)
        fun handlerStartSong(list: MutableList<Song>, pos: Int, context: Context?)
        fun isConnectedInternet(context: Context): Boolean
    }
}

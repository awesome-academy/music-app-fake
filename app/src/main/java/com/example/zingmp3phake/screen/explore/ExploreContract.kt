package com.example.zingmp3phake.screen.explore

import android.content.Context
import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.utils.base.BasePresenter

interface ExploreContract {
    interface View {
        fun displaySuccess(list: MutableList<Song>)
        fun displayFail(msg: String)
        fun displayNoInternet()
    }

    interface Presenter : BasePresenter<View> {
        fun getTrendingSong(context: Context?)
        fun handlerStartSong(list: MutableList<Song>, pos: Int, context: Context?)
    }
}

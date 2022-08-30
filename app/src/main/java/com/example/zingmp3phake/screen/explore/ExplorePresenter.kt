package com.example.zingmp3phake.screen.explore

import android.content.Context
import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.data.repo.SongRepository
import com.example.zingmp3phake.data.repo.resource.Listener
import com.example.zingmp3phake.screen.MusicService
import com.example.zingmp3phake.utils.NetworkUtils

class ExplorePresenter(val songRepo: SongRepository) :
    ExploreContract.Presenter {

    private var view: ExploreContract.View? = null
    var musicService: MusicService = MusicService()

    override fun getTrendingSong(context: Context?) {
        songRepo.getTrendingSong(object : Listener<MutableList<Song>> {
            override fun onSuccess(list: MutableList<Song>) {
                view?.displaySuccess(list)
            }

            override fun onFail(msg: String) {
                view?.displayFail(msg)
            }
        })
    }

    override fun handlerStartSong(list: MutableList<Song>, pos: Int, context: Context?) {
        val song = list.get(pos)
        if (song.isLocal == false && NetworkUtils.isNetworkAvailable(context) == false) {
            view?.displayNoInternet()
            return
        }
        musicService.startSong(list, pos)
    }

    override fun onStart() {
        // TODO("Not yet implemented")
    }

    override fun onStop() {
        // TODO("Not yet implemented")
    }

    override fun setView(view: ExploreContract.View?) {
        this.view = view
    }
}

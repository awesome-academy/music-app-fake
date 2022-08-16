package com.example.zingmp3phake.screen.explore

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.ConnectivityManager
import android.os.IBinder
import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.data.repo.SongRepository
import com.example.zingmp3phake.data.repo.resource.Listener
import com.example.zingmp3phake.screen.MusicService

class ExploreFragmentPresenter(val songRepo: SongRepository, val exploreView: ExploreContract.View) :
    ExploreContract.Presenter {

    private var musicService: MusicService = MusicService()
    private var isConnection = false
    private var postion = 0
    private var serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.LocalBinder
            musicService = binder.getService()
            isConnection = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isConnection = false
        }
    }

    override fun bindService(context: Context) {
        if (isConnection == false) {
            val service = Intent(context, MusicService::class.java)
            context.bindService(service, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun stopService() {
        musicService.unbindService(serviceConnection)
        isConnection = false
    }

    override fun getTrendingSong(context: Context?) {
        songRepo.getTrendingSong(object : Listener<MutableList<Song>> {
            override fun onSuccess(list: MutableList<Song>) {
                exploreView.displaySuccess(list)
            }

            override fun onFail(msg: String) {
                exploreView.displayFail(msg)
            }
        })
    }

    override fun handlerStartSong(list: MutableList<Song>, pos: Int, context: Context?) {
        if (isConnection) {
            musicService.startSong(list, pos)
        } else {
            val service = Intent(context, MusicService::class.java)
            context?.bindService(service, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    fun handleNextSong() {
        postion = (musicService.positions + 1) % musicService.listSongs.size
        musicService.startSong(musicService.listSongs, postion)
    }

    override fun isConnectedInternet(context: Context): Boolean {
        val cm: ConnectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true
        }
        return false
    }
}

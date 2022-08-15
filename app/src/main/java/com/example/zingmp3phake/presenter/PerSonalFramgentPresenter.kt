package com.example.zingmp3phake.presenter

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.data.repo.SongRepo
import com.example.zingmp3phake.data.repo.resource.Listener
import com.example.zingmp3phake.utils.READ_PERMISSION_REQUEST_CODE

class PerSonalFramgentPresenter(
    val songRepo: SongRepo,
    val personalView: IPersonalFragment.View
) : IPersonalFragment.Presenter {

    private var listLocalSong = mutableListOf<Song>()
    private var listRecentSong = mutableListOf<Song>()
    private var listFavoriteSong = mutableListOf<Song>()

    private var musicService: MusicService = MusicService()
    private var isConnection = false
    private var postion = 0
    private var serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.LocalBinder
            musicService = binder.getService()
            isConnection = true
            musicService.startSong(listRecentSong, postion)
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isConnection = false
        }
    }

    override fun getLocalSong(context: AppCompatActivity) {
        if (checkReadPermission(context.applicationContext)) {
            requestReadPermission(context)
        } else {
            songRepo.getSongLocal(
                context.applicationContext,
                object : Listener<MutableList<Song>> {
                    override fun onSuccess(list: MutableList<Song>) {
                        listLocalSong = list
                        personalView.getLocalSongSuccess(list)
                    }

                    override fun onFail(msg: String) {
                        personalView.getLocalSongFail(msg)
                    }
                }
            )
        }
    }

    override fun getRecentSong(context: Context?) {
        songRepo.getSongRecent(
            context,
            object : Listener<MutableList<Song>> {
                override fun onSuccess(list: MutableList<Song>) {
                    personalView.getRecentSong(list)
                    listRecentSong = list
                }

                override fun onFail(msg: String) {
                    // TODO later
                }
            }
        )
    }

    override fun getFavoriteSong() {
        songRepo.getSongFavorite(object : Listener<MutableList<Song>> {
            override fun onSuccess(list: MutableList<Song>) {
                listFavoriteSong = list
            }

            override fun onFail(msg: String) {
                // TODO("Not yet implemented")
            }
        })
    }

    override fun handleStartSong(list: MutableList<Song>, pos: Int, context: Context?) {
        postion = pos
        if (isConnection) {
            musicService.startSong(list, pos)
        } else {
            val service = Intent(context, MusicService::class.java)
            context?.bindService(service, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun stopService() {
        musicService.unbindService(serviceConnection)
        isConnection = false
    }

    private fun checkReadPermission(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
    }

    private fun requestReadPermission(activity: AppCompatActivity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            READ_PERMISSION_REQUEST_CODE
        )
    }
}

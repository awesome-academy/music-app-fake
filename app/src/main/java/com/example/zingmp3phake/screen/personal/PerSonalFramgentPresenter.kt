package com.example.zingmp3phake.screen.personal

import android.Manifest
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.data.repo.SongRepository
import com.example.zingmp3phake.data.repo.resource.Listener
import com.example.zingmp3phake.screen.MusicService
import com.example.zingmp3phake.utils.ACTION_MUSIC
import com.example.zingmp3phake.utils.ACTION_MUSIC_BROADCAST
import com.example.zingmp3phake.utils.MusicAction
import com.example.zingmp3phake.utils.READ_PERMISSION_REQUEST_CODE

class PerSonalFramgentPresenter(
    val songRepo: SongRepository,
    val view: PersonalContract.View
) : PersonalContract.Presenter {

    private var listLocalSong = mutableListOf<Song>()
    private var listRecentSong = mutableListOf<Song>()
    private var listFavoriteSong = mutableListOf<Song>()
    private var musicService: MusicService = MusicService()
    private var isConnection = false
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
    private var localReciver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            when (intent?.getStringExtra(ACTION_MUSIC)) {
                MusicAction.START.name -> {
                    songRepo.local.addSongRecent(
                        musicService.listSongs.get(musicService.positions)
                    )
                    getRecentSong(p0)
                }
                else -> {
                }
            }
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
                        view.getLocalSongSuccess(list)
                    }

                    override fun onFail(msg: String) {
                        view.getLocalSongFail(msg)
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
                    view.getRecentSong(list)
                    listRecentSong = list
                }

                override fun onFail(msg: String) {
                    // TODO later
                }
            }
        )
    }

    override fun getFavoriteSong(context: Context?) {
        songRepo.getSongFavorite(object : Listener<MutableList<Song>> {
            override fun onSuccess(list: MutableList<Song>) {
                listFavoriteSong = list
                view.getFavoriteSongSuccess(list)
            }

            override fun onFail(msg: String) {
                // TODO("Not yet implemented")
            }
        })
    }

    override fun handleStartSong(list: MutableList<Song>, pos: Int, context: Context?) {
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

    override fun bindService(context: Context?) {
        if (isConnection == false) {
            val service = Intent(context, MusicService::class.java)
            context?.bindService(service, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun registerBroadcast(context: Context?) {
        val filter = IntentFilter(ACTION_MUSIC_BROADCAST)
        context?.registerReceiver(localReciver, filter)
    }

    override fun unRegisterBroadcast(context: Context?) {
        context?.unregisterReceiver(localReciver)
    }

    private fun checkReadPermission(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
    }

    private fun requestReadPermission(activity: AppCompatActivity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE
            ),
            READ_PERMISSION_REQUEST_CODE
        )
    }
}

package com.example.zingmp3phake.screen.personal

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.data.repo.SongRepository
import com.example.zingmp3phake.data.repo.resource.Listener
import com.example.zingmp3phake.screen.MusicService
import com.example.zingmp3phake.utils.Constant

class PerSonalPresenter(
    val songRepo: SongRepository
) : PersonalContract.Presenter {

    private var listLocalSong = mutableListOf<Song>()
    private var listRecentSong = mutableListOf<Song>()
    private var listFavoriteSong = mutableListOf<Song>()
    private var view: PersonalContract.View? = null
    var musicService: MusicService = MusicService()

    override fun getLocalSong(context: AppCompatActivity) {
        if (checkReadPermission(context.applicationContext)) {
            requestReadPermission(context)
        } else {
            songRepo.getSongLocal(
                context.applicationContext,
                object : Listener<MutableList<Song>> {
                    override fun onSuccess(list: MutableList<Song>) {
                        listLocalSong = list
                        view?.getLocalSongSuccess(list)
                    }

                    override fun onFail(msg: String) {
                        view?.getSongFail(msg)
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
                    view?.getRecentSong(list)
                    listRecentSong = list
                }

                override fun onFail(msg: String) {
                    view?.getSongFail(msg)
                }
            }
        )
    }

    override fun getFavoriteSong(context: Context?) {
        songRepo.getSongFavorite(object : Listener<MutableList<Song>> {
            override fun onSuccess(list: MutableList<Song>) {
                listFavoriteSong = list
                view?.getFavoriteSongSuccess(list)
            }

            override fun onFail(msg: String) {
                view?.getSongFail(msg)
            }
        })
    }

    override fun handleStartSong(list: MutableList<Song>, pos: Int, context: Context?) {
        musicService.startSong(list, pos)
    }

    override fun addSongRecent() {
        songRepo.local.addSongRecent(
            musicService.listSongs.get(musicService.positions)
        )
    }

    override fun onStart() {
        // TODO("Not yet implemented")
    }

    override fun onStop() {
        // TODO("Not yet implemented")
    }

    override fun setView(view: PersonalContract.View?) {
        this.view = view
    }

    fun checkReadPermission(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
    }

    fun requestReadPermission(activity: AppCompatActivity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE
            ),
            Constant.READ_PERMISSION_REQUEST_CODE
        )
    }
}

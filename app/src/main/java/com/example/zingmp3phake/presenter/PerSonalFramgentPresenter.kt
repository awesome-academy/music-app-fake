package com.example.zingmp3phake.presenter

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.zingmp3phake.ui.DetailPlaylistActivity
import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.data.repo.SongRepo
import com.example.zingmp3phake.data.repo.resource.Listener
import com.example.zingmp3phake.utils.BUNDLE_LIST_KEY
import com.example.zingmp3phake.utils.BUNDLE_TITLE_STRING_KEY
import com.example.zingmp3phake.utils.DATA_KEY
import com.example.zingmp3phake.utils.READ_PERMISSION_REQUEST_CODE
import com.example.zingmp3phake.utils.TITLE_FAVORITE
import com.example.zingmp3phake.utils.TITLE_LOCAL

class PerSonalFramgentPresenter(
    val songRepo: SongRepo,
    val personalView: IPersonalFragment.View
) : IPersonalFragment.Presenter {
    private var context: Context? = null
    private var listLocalSong = mutableListOf<Song>()
    private var listRecentSong = mutableListOf<Song>()
    private var listFavoriteSong = mutableListOf<Song>()

    override fun getLocalSong(context: AppCompatActivity) {
        this.context = context.applicationContext
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

    override fun getRecentSong(context: Context) {
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
        TODO("Not yet implemented")
    }

    override fun handleStartSong(list: MutableList<Song>, pos: Int) {
        // TODO Implement later
    }

    override fun handleClickIntent(title: String) {
        var intent: Intent? = null
        val bundle = Bundle()
        when (title) {
            TITLE_LOCAL -> {
                intent = Intent(context?.applicationContext, DetailPlaylistActivity::class.java)
                bundle.putParcelableArrayList(BUNDLE_LIST_KEY, listLocalSong as ArrayList<Song>)
            }
            TITLE_FAVORITE -> {
                intent = Intent(context?.applicationContext, DetailPlaylistActivity::class.java)
                bundle.putParcelableArrayList(BUNDLE_LIST_KEY, listFavoriteSong as ArrayList<Song>)
            }
        }
        bundle.putString(BUNDLE_TITLE_STRING_KEY, title)
        intent?.putExtra(DATA_KEY, bundle)
        intent?.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context?.startActivity(intent)
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

package com.example.zingmp3phake.screen.personal

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.data.repo.SongRepository
import com.example.zingmp3phake.data.repo.resource.local.LocalSong
import com.example.zingmp3phake.data.repo.resource.remote.RemoteSong
import com.example.zingmp3phake.databinding.FragmentPersonalBinding
import com.example.zingmp3phake.screen.MusicService
import com.example.zingmp3phake.screen.detailplaylist.DetailPlaylistActivity
import com.example.zingmp3phake.utils.Constant
import com.example.zingmp3phake.utils.MusicAction
import com.example.zingmp3phake.utils.NetworkUtils
import com.example.zingmp3phake.utils.base.BaseFragment

class PersonalFragment :
    BaseFragment<FragmentPersonalBinding>(FragmentPersonalBinding::inflate),
    PersonalContract.View,
    RecyclerViewRecentAdapter.ItemClickListener {

    private var listLocalSong = mutableListOf<Song>()
    private var listRecentSong = mutableListOf<Song>()
    private var listFavoriteSong = mutableListOf<Song>()
    private var presenter = PerSonalPresenter(
        SongRepository.getInstance(
            LocalSong.getInstance(),
            RemoteSong.getInstance()
        )
    )
    private var localReciver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            when (intent?.getStringExtra(Constant.ACTION_MUSIC)) {
                MusicAction.START.name -> {
                    presenter.addSongRecent()
                    presenter.getRecentSong(p0)
                }
                else -> {
                }
            }
        }
    }
    private var isConnection = false
    private var serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.LocalBinder
            presenter.musicService = binder.getService()
            isConnection = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isConnection = false
        }
    }
    private val adapterRecentSong = RecyclerViewRecentAdapter(this)

    override fun initView() {
        binding.recyclerViewSongRecent.isNestedScrollingEnabled = true
        binding.recyclerViewSongRecent.adapter = adapterRecentSong
        binding.apply {
            containerLocal.setOnClickListener {
                val intent = Intent(context?.applicationContext, DetailPlaylistActivity::class.java)
                val bundle = Bundle()
                bundle.putParcelableArrayList(
                    Constant.BUNDLE_LIST_KEY,
                    listLocalSong as ArrayList<Song>
                )
                bundle.putString(Constant.BUNDLE_TITLE_STRING_KEY, Constant.TITLE_LOCAL)
                intent?.putExtra(Constant.DATA_KEY, bundle)
                intent?.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context?.startActivity(intent)
            }
            containerFavorite.setOnClickListener {
                presenter?.getFavoriteSong(context)
            }
        }
        val service = Intent(context, MusicService::class.java)
        context?.bindService(service, serviceConnection, Context.BIND_AUTO_CREATE)
        val filter = IntentFilter(Constant.ACTION_MUSIC_BROADCAST)
        context?.registerReceiver(localReciver, filter)
    }

    override fun initData() {
        presenter.setView(this)
        presenter?.getLocalSong(context as AppCompatActivity)
        presenter?.getRecentSong(context)
    }

    override fun getLocalSongSuccess(songs: MutableList<Song>) {
        binding.textNumberOfLocalSong.text = "${songs.size} ${Constant.SONG}"
        listLocalSong = songs
    }

    override fun getSongFail(msg: String) {
        Toast.makeText(context?.applicationContext, msg, Toast.LENGTH_SHORT).show()
    }

    override fun getRecentSong(songs: MutableList<Song>) {
        adapterRecentSong.setData(songs)
        listRecentSong = songs
    }

    override fun getFavoriteSongSuccess(songs: MutableList<Song>) {
        listFavoriteSong = songs
        val intent = Intent(context?.applicationContext, DetailPlaylistActivity::class.java)
        val bundle = Bundle()
        bundle.putParcelableArrayList(Constant.BUNDLE_LIST_KEY, listFavoriteSong as ArrayList<Song>)
        bundle.putString(Constant.BUNDLE_TITLE_STRING_KEY, Constant.TITLE_FAVORITE)
        intent?.putExtra(Constant.DATA_KEY, bundle)
        intent?.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context?.startActivity(intent)
    }

    override fun displayNoInternet() {
        Toast.makeText(context?.applicationContext, Constant.NO_INTERNET, Toast.LENGTH_SHORT).show()
    }

    override fun onItemClick(pos: Int, listSong: MutableList<Song>) {
        val song = listSong.get(pos)
        if (song.isLocal == false && NetworkUtils.isNetworkAvailable(context) == false) {
            Toast.makeText(context, "${Constant.NO_INTERNET}", Toast.LENGTH_SHORT).show()
            return
        }
        presenter?.handleStartSong(listSong, pos, context)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isConnection) {
            presenter.musicService.unbindService(serviceConnection)
            isConnection = false
        }
        context?.unregisterReceiver(localReciver)
    }
}

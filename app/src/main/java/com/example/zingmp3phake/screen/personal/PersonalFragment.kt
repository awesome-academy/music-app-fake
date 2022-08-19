package com.example.zingmp3phake.screen.personal

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.data.repo.SongRepository
import com.example.zingmp3phake.data.repo.resource.local.LocalSong
import com.example.zingmp3phake.data.repo.resource.remote.RemoteSong
import com.example.zingmp3phake.databinding.FragmentPersonalBinding
import com.example.zingmp3phake.screen.detailplaylist.DetailPlaylistActivity
import com.example.zingmp3phake.utils.BUNDLE_LIST_KEY
import com.example.zingmp3phake.utils.BUNDLE_TITLE_STRING_KEY
import com.example.zingmp3phake.utils.DATA_KEY
import com.example.zingmp3phake.utils.SONG
import com.example.zingmp3phake.utils.TITLE_FAVORITE
import com.example.zingmp3phake.utils.TITLE_LOCAL
import com.example.zingmp3phake.utils.base.BaseFragment

class PersonalFragment :
    BaseFragment<FragmentPersonalBinding>(FragmentPersonalBinding::inflate),
    PersonalContract.View,
    RecyclerViewRecentAdapter.ItemClickListener {

    private var listLocalSong = mutableListOf<Song>()
    private var listRecentSong = mutableListOf<Song>()
    private var listFavoriteSong = mutableListOf<Song>()
    private var presenter = PerSonalFramgentPresenter(
        SongRepository.getInstance(
            LocalSong.getInstance(),
            RemoteSong.getInstance()
        ),
        this
    )
    private val adapterRecentSong = RecyclerViewRecentAdapter(this)

    override fun initView() {
        binding.recyclerViewSongRecent.isNestedScrollingEnabled = true
        binding.recyclerViewSongRecent.adapter = adapterRecentSong
        binding.apply {
            containerLocal.setOnClickListener {
                val intent = Intent(context?.applicationContext, DetailPlaylistActivity::class.java)
                val bundle = Bundle()
                bundle.putParcelableArrayList(BUNDLE_LIST_KEY, listLocalSong as ArrayList<Song>)
                bundle.putString(BUNDLE_TITLE_STRING_KEY, TITLE_LOCAL)
                intent?.putExtra(DATA_KEY, bundle)
                intent?.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context?.startActivity(intent)
            }
            containerFavorite.setOnClickListener {
                presenter?.getFavoriteSong(context)
            }
        }
    }

    override fun initData() {
        presenter?.getLocalSong(context as AppCompatActivity)
        presenter?.getRecentSong(context)
        presenter?.registerBroadcast(context)
        presenter?.bindService(context)
    }

    override fun getLocalSongSuccess(songs: MutableList<Song>) {
        binding.textNumberOfLocalSong.text = "${songs.size} $SONG"
        listLocalSong = songs
    }

    override fun getLocalSongFail(msg: String) {
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
        bundle.putParcelableArrayList(BUNDLE_LIST_KEY, listFavoriteSong as ArrayList<Song>)
        bundle.putString(BUNDLE_TITLE_STRING_KEY, TITLE_FAVORITE)
        intent?.putExtra(DATA_KEY, bundle)
        intent?.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context?.startActivity(intent)
    }

    override fun onItemClick(pos: Int, listSong: MutableList<Song>) {
        presenter?.handleStartSong(listSong, pos, context)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.stopService()
        presenter?.unRegisterBroadcast(context)
    }
}

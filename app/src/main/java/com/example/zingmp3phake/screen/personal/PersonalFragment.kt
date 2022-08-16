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
    private var personalPresenter = PerSonalFramgentPresenter(
        SongRepository.getInstance(
            LocalSong.getInstance(),
            RemoteSong.getInstance()
        ),
        this
    )
    private val adapterRv = RecyclerViewRecentAdapter(this)

    override fun initView() {
        binding.recyclerView.isNestedScrollingEnabled = true
        binding.recyclerView.adapter = adapterRv
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
                personalPresenter?.getFavoriteSong(context)
            }
        }
    }

    override fun initData() {
        personalPresenter?.getLocalSong(context as AppCompatActivity)
        personalPresenter?.getRecentSong(context)
        personalPresenter?.registerBroadcast(context)
        personalPresenter?.bindService(context)
    }

    override fun getLocalSongSuccess(list: MutableList<Song>) {
        binding.textviewNumberOfLocalSong.text = "${list.size} $SONG"
        listLocalSong = list
    }

    override fun getLocalSongFail(msg: String) {
        Toast.makeText(context?.applicationContext, msg, Toast.LENGTH_SHORT).show()
    }

    override fun getRecentSong(list: MutableList<Song>) {
        adapterRv.setData(list)
        listRecentSong = list
    }

    override fun getFavoriteSongSuccess(list: MutableList<Song>) {
        listFavoriteSong = listLocalSong
        val intent = Intent(context?.applicationContext, DetailPlaylistActivity::class.java)
        val bundle = Bundle()
        bundle.putParcelableArrayList(BUNDLE_LIST_KEY, listFavoriteSong as ArrayList<Song>)
        bundle.putString(BUNDLE_TITLE_STRING_KEY, TITLE_FAVORITE)
        intent?.putExtra(DATA_KEY, bundle)
        intent?.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context?.startActivity(intent)
    }

    override fun onItemClick(pos: Int, listSong: MutableList<Song>) {
        personalPresenter?.handleStartSong(listSong, pos, context)
    }

    override fun onDestroy() {
        super.onDestroy()
        personalPresenter?.stopService()
        personalPresenter?.unRegisterBroadcast(context)
    }
}

package com.example.zingmp3phake.screen.explore

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.view.View
import android.widget.Toast
import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.data.repo.SongRepository
import com.example.zingmp3phake.data.repo.resource.local.LocalSong
import com.example.zingmp3phake.data.repo.resource.remote.RemoteSong
import com.example.zingmp3phake.databinding.FragmentExploreBinding
import com.example.zingmp3phake.screen.MusicService
import com.example.zingmp3phake.utils.Constant
import com.example.zingmp3phake.utils.NetworkUtils
import com.example.zingmp3phake.utils.base.BaseFragment

class ExploreFragment :
    BaseFragment<FragmentExploreBinding>(FragmentExploreBinding::inflate),
    ExploreContract.View,
    RecyclerViewAdapter.ItemClickListener {
    private lateinit var presenter: ExplorePresenter
    private val adapterExplore = RecyclerViewAdapter(this)
    var isConnection = false
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

    override fun initView() {
        binding.recycleViewTrendingSong.adapter = adapterExplore
    }

    override fun initData() {
        presenter = ExplorePresenter(
            SongRepository.getInstance(
                LocalSong.getInstance(),
                RemoteSong.getInstance(),
            )
        )
        presenter.setView(this)
        if (context?.let { NetworkUtils.isNetworkAvailable(it.applicationContext) } == true) {
            presenter.getTrendingSong(context)
        } else {
            Toast.makeText(context, Constant.NO_INTERNET, Toast.LENGTH_LONG).show()
        }
        if (isConnection == false) {
            val service = Intent(context?.applicationContext, MusicService::class.java)
            context?.bindService(service, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun displaySuccess(list: MutableList<Song>) {
        adapterExplore.setData(list)
        binding.cardview.visibility = View.GONE
    }

    override fun displayFail(msg: String) {
        // TODO later
    }

    override fun displayNoInternet() {
        Toast.makeText(context?.applicationContext, Constant.NO_INTERNET, Toast.LENGTH_SHORT).show()
    }

    override fun onItemClick(pos: Int, listSong: MutableList<Song>) {
        presenter.handlerStartSong(listSong, pos, context)
    }

    override fun onDetach() {
        super.onDetach()
        if (isConnection) {
            presenter.musicService.unbindService(serviceConnection)
            isConnection = false
        }
    }
}

package com.example.zingmp3phake.ui.fragment

import android.widget.Toast
import com.example.zingmp3phake.ui.adapter.RecyclerViewAdapter
import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.data.repo.SongRepo
import com.example.zingmp3phake.data.repo.resource.local.LocalSong
import com.example.zingmp3phake.data.repo.resource.remote.RemoteSong
import com.example.zingmp3phake.databinding.FragmentExploreBinding
import com.example.zingmp3phake.presenter.ExploreFragmentPresenter
import com.example.zingmp3phake.presenter.IExploreFragment
import com.example.zingmp3phake.utils.NO_INTERNET
import com.example.zingmp3phake.utils.base.BaseFragment

class ExploreFragment :
    BaseFragment<FragmentExploreBinding>(FragmentExploreBinding::inflate),
    IExploreFragment.View,
    RecyclerViewAdapter.ItemClickListener {
    private var explorePresenter: ExploreFragmentPresenter? = null
    private val adapterRv = RecyclerViewAdapter(this)

    override fun initView() {
        binding.recycleView.adapter = adapterRv
    }

    override fun initData() {
        explorePresenter = ExploreFragmentPresenter(
            SongRepo.getInstance(
                LocalSong.getInstance(),
                RemoteSong.getInstance(),
            ),
            this
        )
        if (context?.let { explorePresenter?.isConnectedInternet(it.applicationContext) } == true) {
            explorePresenter?.getTrendingSong(context)
        } else {
            Toast.makeText(context, NO_INTERNET, Toast.LENGTH_LONG).show()
        }
    }

    override fun displaySuccess(list: MutableList<Song>) {
        adapterRv.setData(list)
    }

    override fun displayFail(msg: String) {
        // TODO later
    }

    override fun onItemClick(pos: Int, listSong: MutableList<Song>) {
        explorePresenter?.handlerStartSong(listSong, pos, context)
    }

    override fun onDetach() {
        super.onDetach()
        explorePresenter?.stopService()
    }
}

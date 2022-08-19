package com.example.zingmp3phake.screen.explore

import android.view.View
import android.widget.Toast
import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.data.repo.SongRepository
import com.example.zingmp3phake.data.repo.resource.local.LocalSong
import com.example.zingmp3phake.data.repo.resource.remote.RemoteSong
import com.example.zingmp3phake.databinding.FragmentExploreBinding
import com.example.zingmp3phake.utils.NO_INTERNET
import com.example.zingmp3phake.utils.base.BaseFragment

class ExploreFragment :
    BaseFragment<FragmentExploreBinding>(FragmentExploreBinding::inflate),
    ExploreContract.View,
    RecyclerViewAdapter.ItemClickListener {
    private var presenter: ExploreFragmentPresenter? = null
    private val adapterExplore = RecyclerViewAdapter(this)

    override fun initView() {
        binding.recycleViewTrendingSong.adapter = adapterExplore
    }

    override fun initData() {
        presenter = ExploreFragmentPresenter(
            SongRepository.getInstance(
                LocalSong.getInstance(),
                RemoteSong.getInstance(),
            ),
            this
        )
        if (context?.let { presenter?.isConnectedInternet(it.applicationContext) } == true) {
            presenter?.getTrendingSong(context)
        } else {
            Toast.makeText(context, NO_INTERNET, Toast.LENGTH_LONG).show()
        }
    }

    override fun displaySuccess(list: MutableList<Song>) {
        adapterExplore.setData(list)
        binding.cardview.visibility = View.GONE
    }

    override fun displayFail(msg: String) {
        // TODO later
    }

    override fun onItemClick(pos: Int, listSong: MutableList<Song>) {
        presenter?.handlerStartSong(listSong, pos, context)
    }

    override fun onDetach() {
        super.onDetach()
        presenter?.stopService()
    }
}

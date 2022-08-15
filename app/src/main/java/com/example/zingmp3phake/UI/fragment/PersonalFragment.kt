package com.example.zingmp3phake.ui.fragment

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.zingmp3phake.ui.adapter.RecyclerViewRecentAdapter
import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.data.repo.SongRepo
import com.example.zingmp3phake.data.repo.resource.local.LocalSong
import com.example.zingmp3phake.data.repo.resource.remote.RemoteSong
import com.example.zingmp3phake.databinding.FragmentPersonalBinding
import com.example.zingmp3phake.presenter.IPersonalFragment
import com.example.zingmp3phake.presenter.PerSonalFramgentPresenter
import com.example.zingmp3phake.utils.SONG
import com.example.zingmp3phake.utils.TITLE_FAVORITE
import com.example.zingmp3phake.utils.TITLE_LOCAL
import com.example.zingmp3phake.utils.base.BaseFragment

class PersonalFragment :
    BaseFragment<FragmentPersonalBinding>(FragmentPersonalBinding::inflate),
    IPersonalFragment.View,
    RecyclerViewRecentAdapter.ItemClickListener {

    private var personalPresenter: PerSonalFramgentPresenter? = null
    private val adapterRv = RecyclerViewRecentAdapter(this)

    override fun initView() {
        binding.recyclerView.isNestedScrollingEnabled = true
        binding.recyclerView.adapter = adapterRv
        binding.apply {
            containerLocal.setOnClickListener {
                personalPresenter?.handleClickIntent(TITLE_LOCAL)
            }
            containerFavorite.setOnClickListener {
                personalPresenter?.handleClickIntent(TITLE_FAVORITE)
            }
        }
    }

    override fun initData() {
        personalPresenter = PerSonalFramgentPresenter(
            SongRepo.getInstance(
                LocalSong.getInstance(),
                RemoteSong.getInstance()
            ),
            this
        )
        personalPresenter?.getLocalSong(context as AppCompatActivity)
    }

    override fun getLocalSongSuccess(list: MutableList<Song>) {
        binding.textviewNumberOfLocalSong.text = "${list.size} $SONG"
    }

    override fun getLocalSongFail(msg: String) {
        Toast.makeText(context?.applicationContext, msg, Toast.LENGTH_SHORT).show()
    }

    override fun getRecentSong(list: MutableList<Song>) {
        adapterRv.setData(list)
    }

    override fun onItemClick(pos: Int, listSong: MutableList<Song>) {
        personalPresenter?.handleStartSong(listSong, pos)
    }
}

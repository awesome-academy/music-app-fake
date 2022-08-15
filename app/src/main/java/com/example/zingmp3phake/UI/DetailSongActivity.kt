package com.example.zingmp3phake.ui

import androidx.fragment.app.Fragment
import com.example.zingmp3phake.databinding.ActivityDetailSongBinding
import com.example.zingmp3phake.ui.adapter.ViewPagerDetailSongAdapter
import com.example.zingmp3phake.ui.fragment.DetailImageSongFragment
import com.example.zingmp3phake.ui.fragment.LyricSongFragment
import com.example.zingmp3phake.utils.base.BaseActivity

class DetailSongActivity :
    BaseActivity<ActivityDetailSongBinding>(ActivityDetailSongBinding::inflate) {

    override fun initData() {
        // TODO later
    }

    override fun initView() {
        binding.apply {
            viewPager.adapter = ViewPagerDetailSongAdapter(
                supportFragmentManager,
                listOf<Fragment>(DetailImageSongFragment(), LyricSongFragment())
            )
        }
    }
}

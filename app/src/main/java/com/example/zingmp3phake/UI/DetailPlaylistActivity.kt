package com.example.zingmp3phake.ui

import com.example.zingmp3phake.R
import com.example.zingmp3phake.databinding.ActivityDetailPlaylistBinding
import com.example.zingmp3phake.utils.base.BaseActivity

class DetailPlaylistActivity :
    BaseActivity<ActivityDetailPlaylistBinding>(ActivityDetailPlaylistBinding::inflate) {

    override fun initData() {
        // TODO later
    }

    override fun initView() {
        setContentView(R.layout.activity_detail_playlist)
    }
}

package com.example.zingmp3phake.screen.search

import com.example.zingmp3phake.databinding.ActivitySearchBinding
import com.example.zingmp3phake.utils.base.BaseActivity

class SearchActivity : BaseActivity<ActivitySearchBinding>(ActivitySearchBinding::inflate) {

    override fun initData() {
        // TODO never implement
    }

    override fun initView() {
        binding.apply {
            searchView.requestFocus()
        }
    }

    override fun handleEvent() {
        // TODO("Not yet implemented")
    }
}

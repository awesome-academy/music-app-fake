package com.example.zingmp3phake.screen

import android.content.Intent
import com.example.zingmp3phake.databinding.ActivityLuanchBinding
import com.example.zingmp3phake.screen.mainapp.MainAppActivity
import com.example.zingmp3phake.utils.TIME_DELAY_LAUNCH
import com.example.zingmp3phake.utils.base.BaseActivity
import com.example.zingmp3phake.utils.handler

class LuanchActivity : BaseActivity<ActivityLuanchBinding>(ActivityLuanchBinding::inflate) {
    override fun initData() {
        // TODO nerver implement
    }

    override fun initView() {
        handler.postDelayed({
            val intent = Intent(this, MainAppActivity::class.java)
            startActivity(intent)
            finish()
        }, TIME_DELAY_LAUNCH)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    override fun handleEvent() {
        // TODO("Not yet implemented")
    }
}

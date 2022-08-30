package com.example.zingmp3phake.screen.detailimage

import androidx.core.net.toUri
import com.example.zingmp3phake.databinding.FragmentSongImageDetailBinding
import com.example.zingmp3phake.utils.TIME_DELAY_FOR_LOAD
import com.example.zingmp3phake.utils.TIME_SLEEP_100
import com.example.zingmp3phake.utils.base.BaseFragment
import com.example.zingmp3phake.utils.handler
import com.example.zingmp3phake.utils.loadByGlide

class DetailImageSongFragment :
    BaseFragment<FragmentSongImageDetailBinding>(FragmentSongImageDetailBinding::inflate) {
    private var stateRotate = 0
    override fun initView() {
        // TODO("Not yet implemented")
    }

    override fun initData() {
        // TODO("Not yet implemented")
    }

    fun displayImage(imgUrl: String) {
        stateRotate = 0
        handler.postDelayed({
            binding.imageviewSong.loadByGlide(binding.root.context, imgUrl.toUri())
        }, TIME_DELAY_FOR_LOAD)
    }

    private fun startAnim() {
        handler.post(object : Runnable {
            override fun run() {
                binding.imageviewSong.rotation = stateRotate++.toFloat()
                handler.removeCallbacks(this)
                handler.postDelayed(this, TIME_SLEEP_100)
            }
        })
    }

    fun stopSong() {
        handler.removeCallbacksAndMessages(null)
    }

    fun playSong() {
        startAnim()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}

package com.example.zingmp3phake.screen.detaillyric

import com.example.zingmp3phake.databinding.FragmentSongLyricsDetailBinding
import com.example.zingmp3phake.utils.N0_LYRIC
import com.example.zingmp3phake.utils.TIME_DELAY_FOR_LOAD
import com.example.zingmp3phake.utils.base.BaseFragment
import com.example.zingmp3phake.utils.handler

class DetailLyricSongFragment :
    BaseFragment<FragmentSongLyricsDetailBinding>(FragmentSongLyricsDetailBinding::inflate) {

    override fun initView() {
        // TODO("Not yet implemented")
    }

    override fun initData() {
        // TODO("Not yet implemented")
    }

    fun displayLyrics(lyrics: MutableList<String>) {
        handler.postDelayed({
            var lyric = ""
            for (x in lyrics) {
                lyric += x + "\n"
            }
            if (lyric.length == 0) binding.textviewLyric.text = N0_LYRIC
            else binding.textviewLyric.text = lyric
        }, TIME_DELAY_FOR_LOAD)
    }
}

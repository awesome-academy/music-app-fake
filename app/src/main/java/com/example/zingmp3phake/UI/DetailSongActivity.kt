package com.example.zingmp3phake.UI

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.zingmp3phake.UI.adapter.ViewPagerDetailSongAdapter
import com.example.zingmp3phake.databinding.ActivityDetailSongBinding

class DetailSongActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailSongBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailSongBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            viewPager.adapter = ViewPagerDetailSongAdapter(supportFragmentManager)
        }
    }
}

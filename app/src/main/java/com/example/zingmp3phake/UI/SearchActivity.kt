package com.example.zingmp3phake.UI

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.zingmp3phake.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            // TODO later
        }
    }
}

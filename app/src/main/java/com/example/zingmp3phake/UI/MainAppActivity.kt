package com.example.zingmp3phake.UI

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.zingmp3phake.databinding.ActivityMainAppBinding

class MainAppActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainAppBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainAppBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}

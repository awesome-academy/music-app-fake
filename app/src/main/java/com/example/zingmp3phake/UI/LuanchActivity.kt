package com.example.zingmp3phake.UI

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.zingmp3phake.R
import com.example.zingmp3phake.utils.TIME_DELAY_LAUNCH
import com.example.zingmp3phake.utils.handler

class LuanchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_luanch)
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
}

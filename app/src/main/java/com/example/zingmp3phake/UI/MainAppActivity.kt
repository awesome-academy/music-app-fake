package com.example.zingmp3phake.ui

import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.zingmp3phake.R
import com.example.zingmp3phake.ui.adapter.ViewPagerMainAdapter
import com.example.zingmp3phake.ui.fragment.ExploreFragment
import com.example.zingmp3phake.ui.fragment.PersonalFragment
import com.example.zingmp3phake.databinding.ActivityMainAppBinding
import com.example.zingmp3phake.utils.base.BaseActivity

class MainAppActivity : BaseActivity<ActivityMainAppBinding>(ActivityMainAppBinding::inflate) {
    override fun initData() {
        // TODO later
    }

    override fun initView() {
        binding.apply {
            viewPagerMain.adapter = ViewPagerMainAdapter(
                supportFragmentManager,
                listOf<Fragment>(ExploreFragment(), PersonalFragment())
            )
            navBar.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.menu_presonal -> {
                        binding.viewPagerMain.currentItem = 1
                        return@setOnItemSelectedListener true
                    }
                    else -> {
                        binding.viewPagerMain.currentItem = 0
                        return@setOnItemSelectedListener true
                    }
                }
            }
            viewPagerMain.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    // TODO nerver implement
                }

                override fun onPageSelected(position: Int) {
                    if (position == 0) navBar.menu.findItem(R.id.menu_explore).isChecked = true
                    else navBar.menu.findItem(R.id.menu_presonal).isChecked = true
                }

                override fun onPageScrollStateChanged(state: Int) {
                    // TODO nerver implement
                }
            })
        }
    }
}

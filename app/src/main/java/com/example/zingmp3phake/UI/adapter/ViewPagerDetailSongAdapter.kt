package com.example.zingmp3phake.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class ViewPagerDetailSongAdapter(fm: FragmentManager, val listFrag: List<Fragment>) :
    FragmentStatePagerAdapter(fm) {
    override fun getCount(): Int {
        return listFrag.size
    }

    override fun getItem(position: Int): Fragment {
        return listFrag.get(position)
    }
}

package com.example.zingmp3phake.screen.mainapp

import android.content.ContentUris
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.zingmp3phake.R
import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.data.repo.SongRepository
import com.example.zingmp3phake.data.repo.resource.local.LocalSong
import com.example.zingmp3phake.data.repo.resource.remote.RemoteSong
import com.example.zingmp3phake.databinding.ActivityMainAppBinding
import com.example.zingmp3phake.screen.detailsong.DetailSongActivity
import com.example.zingmp3phake.screen.explore.ExploreFragment
import com.example.zingmp3phake.screen.personal.PersonalFragment
import com.example.zingmp3phake.screen.search.SearchActivity
import com.example.zingmp3phake.utils.MEDIA_EXTERNAL_AUDIO_URI
import com.example.zingmp3phake.utils.base.BaseActivity

class MainAppActivity :
    BaseActivity<ActivityMainAppBinding>(ActivityMainAppBinding::inflate),
    MainContract.View {

    private val mainActivityPresenter = MainActivityPresenter(
        SongRepository.getInstance(
            LocalSong.getInstance(),
            RemoteSong.getInstance()
        ),
        this
    )

    override fun initData() {
        mainActivityPresenter.bindService(applicationContext)
        mainActivityPresenter.registerBroadcast()
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
                    // TODO never implement
                }
            })
            buttonFavorite.setOnClickListener {
                mainActivityPresenter.handleFavoriteSong()
            }
            buttonPlay.setOnClickListener {
                mainActivityPresenter.handlePlayOrPauseSong()
            }
            buttonNext.setOnClickListener {
                mainActivityPresenter.handleNextSong()
            }
            containerStateSong.setOnClickListener {
                val intent = Intent(applicationContext, DetailSongActivity::class.java)
                startActivity(intent)
            }
            searchView.setOnClickListener {
                val intent = Intent(applicationContext, SearchActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onStartSong(song: Song) {
        binding.apply {
            textviewSongName.text = song.songInfo.songName
            textviewArtistName.text = song.songInfo.songArtist
            containerStateSong.visibility = View.VISIBLE
            buttonPlay.setImageResource(R.drawable.ic_play_24)
            if (song.isFavorite) buttonFavorite.setImageResource(R.drawable.ic_favorite_24)
            else buttonFavorite.setImageResource(R.drawable.ic_unfavorite_24)
            if (song.isLocal) {
                val imgUri = ContentUris.withAppendedId(
                    Uri.parse(MEDIA_EXTERNAL_AUDIO_URI),
                    song.songInfo.songImg.toLong()
                )
                Glide.with(applicationContext)
                    .load(imgUri)
                    .placeholder(R.drawable.imgzingmp3logo)
                    .into(cimg)
            } else {
                Glide.with(applicationContext)
                    .load(song.songInfo.songImg)
                    .placeholder(R.drawable.imgzingmp3logo)
                    .into(cimg)
            }
        }
    }

    override fun onPlaySong() {
        binding.buttonPlay.setImageResource(R.drawable.ic_play_24)
    }

    override fun onPauseSong() {
        binding.buttonPlay.setImageResource(R.drawable.ic_pause_24)
    }

    override fun displayFavotite() {
        binding.buttonFavorite.setImageResource(R.drawable.ic_favorite_24)
    }

    override fun displayUnFavorite() {
        binding.buttonFavorite.setImageResource(R.drawable.ic_unfavorite_24)
    }

    override fun onDestroy() {
        mainActivityPresenter.unBindService()
        mainActivityPresenter.unRegisterBroadcast()
        super.onDestroy()
    }
}

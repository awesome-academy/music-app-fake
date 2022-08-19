package com.example.zingmp3phake.screen.mainapp

import android.content.ContentUris
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
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
import com.example.zingmp3phake.utils.loadByGlide

class MainAppActivity :
    BaseActivity<ActivityMainAppBinding>(ActivityMainAppBinding::inflate),
    MainContract.View {

    private lateinit var presenter: MainActivityPresenter

    override fun initData() {
        presenter = MainActivityPresenter(
            SongRepository.getInstance(
                LocalSong.getInstance(),
                RemoteSong.getInstance()
            ),
            this
        )
    }

    override fun initView() {
        // TODO later
    }

    override fun onStartSong(song: Song) {
        binding.apply {
            textSongName.text = song.songInfo.songName
            textArtistName.text = song.songInfo.songArtist
            containerStateSong.visibility = View.VISIBLE
            buttonPlay.setImageResource(R.drawable.ic_play_24)
            if (song.isFavorite) buttonFavorite.setImageResource(R.drawable.ic_favorite_24)
            else buttonFavorite.setImageResource(R.drawable.ic_unfavorite_24)
            if (song.isLocal) {
                val imgUri = ContentUris.withAppendedId(
                    Uri.parse(MEDIA_EXTERNAL_AUDIO_URI),
                    song.songInfo.songImg.toLong()
                )
                circleImageSong.loadByGlide(applicationContext, imgUri)
            } else {
                circleImageSong.loadByGlide(applicationContext, song.songInfo.songImg.toUri())
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
        presenter.unBindService()
        presenter.unRegisterBroadcast()
        super.onDestroy()
    }

    override fun handleEvent() {
        presenter.bindService(applicationContext)
        presenter.registerBroadcast()
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
                presenter.handleFavoriteSong()
            }
            buttonPlay.setOnClickListener {
                presenter.handlePlayOrPauseSong()
            }
            buttonNext.setOnClickListener {
                presenter.handleNextSong()
            }
            containerStateSong.setOnClickListener {
                val intent = Intent(applicationContext, DetailSongActivity::class.java)
                startActivity(intent)
            }
            containerSearchview.setOnClickListener {
                val intent = Intent(applicationContext, SearchActivity::class.java)
                startActivity(intent)
            }
        }
    }
}

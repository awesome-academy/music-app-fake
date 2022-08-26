package com.example.zingmp3phake.screen.detailsong

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.example.zingmp3phake.R
import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.data.repo.SongRepository
import com.example.zingmp3phake.data.repo.resource.local.LocalSong
import com.example.zingmp3phake.data.repo.resource.remote.RemoteSong
import com.example.zingmp3phake.databinding.ActivityDetailSongBinding
import com.example.zingmp3phake.screen.MusicService
import com.example.zingmp3phake.screen.detailimage.DetailImageSongFragment
import com.example.zingmp3phake.screen.detaillyric.DetailLyricSongFragment
import com.example.zingmp3phake.utils.Constant
import com.example.zingmp3phake.utils.MusicAction
import com.example.zingmp3phake.utils.START_TIME
import com.example.zingmp3phake.utils.TIME_DELAY_FOR_LOAD
import com.example.zingmp3phake.utils.base.BaseActivity
import com.example.zingmp3phake.utils.getTimetoMiliSecond
import com.example.zingmp3phake.utils.getTimetoSecond
import com.example.zingmp3phake.utils.handler
import kotlin.random.Random

class DetailSongActivity :
    BaseActivity<ActivityDetailSongBinding>(ActivityDetailSongBinding::inflate),
    DetailSongContract.View {

    private lateinit var presenter: DetailSongActivityPresenter
    private val detailImageFragment = DetailImageSongFragment()
    private val lyricsDetailFragment = DetailLyricSongFragment()
    var localReciver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            when (intent?.getStringExtra(Constant.ACTION_MUSIC)) {
                MusicAction.START.name -> {
                    handler.removeCallbacksAndMessages(null)
                    presenter.getTimeforView()
                    presenter.getCurrentSong()
                }
                MusicAction.PLAYORPAUSE.name -> {
                    handler.removeCallbacksAndMessages(null)
                    handler.postDelayed({
                        presenter.getTimeforView()
                        presenter.handlePlayOrPause()
                    }, TIME_DELAY_FOR_LOAD)
                }
                MusicAction.NEXT.name -> {
                    handler.removeCallbacksAndMessages(null)
                    handler.postDelayed({
                        presenter.getCurrentSong()
                        presenter.getTimeforView()
                    }, TIME_DELAY_FOR_LOAD)
                }
                MusicAction.PERVIOUS.name -> {
                    handler.removeCallbacksAndMessages(null)
                    handler.postDelayed({
                        presenter.getCurrentSong()
                        presenter.getTimeforView()
                    }, TIME_DELAY_FOR_LOAD)
                }
                else -> {}
            }
        }
    }

    override fun initData() {
        presenter = DetailSongActivityPresenter(
            SongRepository.getInstance(
                LocalSong.getInstance(),
                RemoteSong.getInstance()
            )
        )
        presenter.setView(this)
    }

    override fun initView() {
        binding.apply {
            viewPager.adapter = ViewPagerDetailSongAdapter(
                supportFragmentManager,
                listOf<Fragment>(detailImageFragment, lyricsDetailFragment)
            )
        }
    }

    override fun displayCurrentSong(song: Song) {
        binding.apply {
            textviewSongName.text = song.songInfo.songName
            textviewArtistName.text = song.songInfo.songArtist
            textviewEndTime.text = getTimetoSecond(song.songInfo.duration)
            textviewStartTime.text = START_TIME
            textviewEndTime.text = getTimetoMiliSecond(song.songInfo.duration)
            seekbar.max = song.songInfo.duration
            if (song.isFavorite) buttonFavo.setImageResource(R.drawable.ic_favorite_40)
            else buttonFavo.setImageResource(R.drawable.ic_unfavorite_40)
        }
        if (song.isLocal) {
            val imgUri = ContentUris.withAppendedId(
                Uri.parse(Constant.MEDIA_EXTERNAL_AUDIO_URI),
                song.songInfo.songImg.toLong()
            )
            detailImageFragment.displayImage(imgUri.toString())
        } else detailImageFragment.displayImage(song.songInfo.songImg)
        presenter.getLyrics()
    }

    override fun displayCurrentTimeSong(time: Int) {
        binding.apply {
            seekbar.progress = time
            textviewStartTime.text = getTimetoMiliSecond(time)
        }
    }

    override fun displayLyricSong(lyrics: MutableList<String>) {
        if (lyrics.size > 0)
            lyricsDetailFragment.displayLyrics(lyrics)
    }

    override fun displayPlayOrPause(isPlaying: Boolean) {
        if (isPlaying) {
            binding.buttonPlay.setImageResource(R.drawable.ic_play_40)
            detailImageFragment.playSong()
        } else {
            binding.buttonPlay.setImageResource(R.drawable.ic_pause_40)
            detailImageFragment.stopSong()
        }
    }

    override fun displayFavorite(isFavorite: Boolean) {
        if (isFavorite) binding.buttonFavo.setImageResource(R.drawable.ic_unfavorite_40)
        else binding.buttonFavo.setImageResource(R.drawable.ic_favorite_40)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.apply {
            unregisterReceiver(localReciver)
            unbindService(serviceConnection)
        }
        handler.removeCallbacksAndMessages(null)
    }

    private fun getPendingIntent(action: String): PendingIntent? {
        val intent = Intent(Constant.ACTION_MUSIC_BROADCAST)
        intent.putExtra(Constant.ACTION_MUSIC, action)
        return PendingIntent.getBroadcast(
            applicationContext,
            Random.nextInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun handleEvent() {
        presenter.apply {
            val intent = Intent(applicationContext.applicationContext, MusicService::class.java)
            bindService(intent, presenter.serviceConnection, Context.BIND_AUTO_CREATE)
            val filter = IntentFilter(Constant.ACTION_MUSIC_BROADCAST)
            registerReceiver(localReciver, filter)
            val service = Intent(applicationContext, MusicService::class.java)
            bindService(service, serviceConnection, Context.BIND_AUTO_CREATE)
        }
        binding.apply {
            buttonBack.setOnClickListener {
                finish()
            }
            buttonPlay.setOnClickListener {
                getPendingIntent(MusicAction.PLAYORPAUSE.name)?.send()
            }
            buttonFavo.setOnClickListener {
                getPendingIntent(MusicAction.FAVORITE.name)?.send()
                presenter.handleEventFavorite()
            }
            buttonNext.setOnClickListener {
                getPendingIntent(MusicAction.NEXT.name)?.send()
                binding.buttonPlay.setImageResource(R.drawable.ic_play_40)
            }
            buttonPre.setOnClickListener {
                getPendingIntent(MusicAction.PERVIOUS.name)?.send()
                binding.buttonPlay.setImageResource(R.drawable.ic_play_40)
            }
            seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    // TODO("Not yet implemented")
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    // TODO("Not yet implemented")
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    if (p0 != null) {
                        presenter.handleChangeSeekBar(p0.progress)
                    }
                }
            })
        }
    }
}

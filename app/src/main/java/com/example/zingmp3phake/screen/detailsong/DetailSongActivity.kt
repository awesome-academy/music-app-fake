package com.example.zingmp3phake.screen.detailsong

import android.app.PendingIntent
import android.content.ContentUris
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
import com.example.zingmp3phake.screen.detailimage.DetailImageSongFragment
import com.example.zingmp3phake.screen.detaillyric.DetailLyricSongFragment
import com.example.zingmp3phake.utils.ACTION_MUSIC
import com.example.zingmp3phake.utils.ACTION_MUSIC_BROADCAST
import com.example.zingmp3phake.utils.MEDIA_EXTERNAL_AUDIO_URI
import com.example.zingmp3phake.utils.MusicAction
import com.example.zingmp3phake.utils.START_TIME
import com.example.zingmp3phake.utils.base.BaseActivity
import com.example.zingmp3phake.utils.getTimetoMiliSecond
import com.example.zingmp3phake.utils.getTimetoSecond
import com.example.zingmp3phake.utils.handler
import kotlin.random.Random

class DetailSongActivity :
    BaseActivity<ActivityDetailSongBinding>(ActivityDetailSongBinding::inflate),
    DetailSongContract.View {

    private lateinit var detailSongPresenter: DetailSongActivityPresenter
    private val detailImageFragment = DetailImageSongFragment()
    private val lyricsDetailFragment = DetailLyricSongFragment()

    override fun initData() {
        detailSongPresenter = DetailSongActivityPresenter(
            this,
            SongRepository.getInstance(
                LocalSong.getInstance(),
                RemoteSong.getInstance()
            )
        )
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
                Uri.parse(MEDIA_EXTERNAL_AUDIO_URI),
                song.songInfo.songImg.toLong()
            )
            detailImageFragment.displayImage(imgUri.toString())
        } else detailImageFragment.displayImage(song.songInfo.songImg)
        detailSongPresenter.getLyrics()
    }

    override fun displayCurrentTimeSong(time: Int) {
        binding.apply {
            seekbar.progress = time
            textviewStartTime.text = getTimetoMiliSecond(time)
        }
    }

    override fun displayLyricSong(lyrics: MutableList<String>) {
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
        detailSongPresenter.apply {
            unBindService()
            unregisterReceiver(localReciver)
        }
        handler.removeCallbacksAndMessages(null)
    }

    private fun getPendingIntent(action: String): PendingIntent? {
        val intent = Intent(ACTION_MUSIC_BROADCAST)
        intent.putExtra(ACTION_MUSIC, action)
        return PendingIntent.getBroadcast(
            applicationContext,
            Random.nextInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun handleEvent() {
        detailSongPresenter.apply {
            bindService(applicationContext)
            val filter = IntentFilter(ACTION_MUSIC_BROADCAST)
            registerReceiver(localReciver, filter)
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
                detailSongPresenter.handleEventFavorite()
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
                        detailSongPresenter.handleChangeSeekBar(p0.progress)
                    }
                }
            })
        }
    }
}

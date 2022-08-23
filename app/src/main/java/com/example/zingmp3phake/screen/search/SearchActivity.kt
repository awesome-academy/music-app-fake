package com.example.zingmp3phake.screen.search

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zingmp3phake.R
import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.data.repo.SongRepository
import com.example.zingmp3phake.data.repo.resource.local.LocalSong
import com.example.zingmp3phake.data.repo.resource.remote.RemoteSong
import com.example.zingmp3phake.databinding.ActivitySearchBinding
import com.example.zingmp3phake.screen.detailsong.DetailSongActivity
import com.example.zingmp3phake.utils.Constant
import com.example.zingmp3phake.utils.MusicAction
import com.example.zingmp3phake.utils.TIME_DELAY_FOR_LOAD
import com.example.zingmp3phake.utils.base.BaseActivity
import com.example.zingmp3phake.utils.handler
import com.example.zingmp3phake.utils.loadByGlide
import kotlin.random.Random

class SearchActivity :
    BaseActivity<ActivitySearchBinding>(ActivitySearchBinding::inflate),
    SearchContract.View,
    RecyclerViewSearchAdapter.ItemClickListener {

    private lateinit var presenter: SearchPresenter
    private val recyclerViewSearchAdapter = RecyclerViewSearchAdapter(this)
    private var isEndPage = false
    private var isLoading = false
    private var isFistLoad = false
    private var localReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            when (intent?.getStringExtra(Constant.ACTION_MUSIC)) {
                MusicAction.START.name -> {
                    handler.removeCallbacksAndMessages(null)
                    presenter.getCurrentSong()
                }
                MusicAction.PLAYORPAUSE.name -> {
                    handler.removeCallbacksAndMessages(null)
                    handler.postDelayed({
                        presenter.handlePlayOrPauseSong()
                    }, TIME_DELAY_FOR_LOAD)
                }
                MusicAction.NEXT.name -> {
                    handler.removeCallbacksAndMessages(null)
                    handler.postDelayed({
                        presenter.getCurrentSong()
                    }, TIME_DELAY_FOR_LOAD)
                }
                MusicAction.FAVORITE.name -> {
                    handler.removeCallbacksAndMessages(null)
                    handler.postDelayed({
                        presenter.handleFavorite()
                    }, TIME_DELAY_FOR_LOAD)
                }
                else -> {}
            }
        }
    }

    override fun initData() {
        presenter = SearchPresenter(
            SongRepository.getInstance(LocalSong.getInstance(), RemoteSong.getInstance())
        )
        presenter.setView(this)
    }

    override fun initView() {
        binding.apply {
            searchView.requestFocus()
            recycleViewSearch.adapter = recyclerViewSearchAdapter
        }
    }

    override fun handleEvent() {
        presenter.apply {
            bindService(applicationContext)
            val filter = IntentFilter(Constant.ACTION_MUSIC_BROADCAST)
            registerReceiver(localReceiver, filter)
        }
        binding.apply {
            buttonBack.setOnClickListener {
                finish()
            }
            buttonNext.setOnClickListener {
                SendBroadcast().sendPendingIntent(MusicAction.NEXT.name)
                binding.buttonPlay.setImageResource(R.drawable.ic_play_24)
            }
            buttonPlay.setOnClickListener {
                SendBroadcast().sendPendingIntent(MusicAction.PLAYORPAUSE.name)
            }
            buttonFavorite.setOnClickListener {
                SendBroadcast().sendPendingIntent(MusicAction.FAVORITE.name)
            }
            containerStateSong.setOnClickListener {
                val intent = Intent(applicationContext, DetailSongActivity::class.java)
                startActivity(intent)
            }
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(text: String?): Boolean {
                    isFistLoad = false
                    isLoading = false
                    isEndPage = false
                    recyclerViewSearchAdapter.setData(mutableListOf<Song>())
                    containerLoading.visibility = View.VISIBLE
                    presenter.getFisrtResultSearch(text)
                    return true
                }

                override fun onQueryTextChange(p0: String?) = false
            })
            val linearLayoutManager = recycleViewSearch.layoutManager as LinearLayoutManager
            recycleViewSearch.addOnScrollListener(object : ScrollListenner(linearLayoutManager) {
                override fun loadMore() {
                    isLoading = true
                    presenter.getMoreResultSearch()
                }

                override fun isLoading() = isLoading

                override fun isEndPage() = isEndPage
            })
        }
    }

    override fun displayCurrentSong(song: Song) {
        if (song.songInfo.songid == null) return
        binding.apply {
            textSongName.text = song.songInfo.songName
            textArtistName.text = song.songInfo.songArtist
            containerStateSong.visibility = View.VISIBLE
            if (song.isFavorite) buttonFavorite.setImageResource(R.drawable.ic_favorite_24)
            else buttonFavorite.setImageResource(R.drawable.ic_unfavorite_24)
            if (song.isLocal) {
                val imgUri = ContentUris.withAppendedId(
                    Uri.parse(Constant.MEDIA_EXTERNAL_AUDIO_URI),
                    song.songInfo.songImg.toLong()
                )
                circleImageSong.loadByGlide(applicationContext, imgUri)
            } else {
                circleImageSong.loadByGlide(applicationContext, song.songInfo.songImg.toUri())
            }
        }
    }

    override fun displayFavorite(isFavorite: Boolean) {
        binding.apply {
            if (isFavorite) buttonFavorite.setImageResource(R.drawable.ic_favorite_24)
            else buttonFavorite.setImageResource(R.drawable.ic_unfavorite_24)
        }
    }

    override fun displayPlayOrPause(isPlaying: Boolean) {
        binding.apply {
            if (isPlaying) buttonPlay.setImageResource(R.drawable.ic_play_24)
            else buttonPlay.setImageResource(R.drawable.ic_pause_24)
        }
    }

    override fun displayResultSearch(songs: MutableList<Song>) {
        if (isFistLoad == false) {
            if (songs.size == 0) {
                binding.progressLoading.visibility = View.GONE
                binding.textNoData.visibility = View.VISIBLE
            } else {
                binding.containerLoading.visibility = View.GONE
                recyclerViewSearchAdapter.setData(songs)
                if (songs.size < Constant.PAGE_SIZE) isEndPage = true
                else recyclerViewSearchAdapter.addFooterLoading()
                isFistLoad = true
            }
        } else {
            recyclerViewSearchAdapter.addData(songs)
            if (songs.size < Constant.PAGE_SIZE) isEndPage = true
            else recyclerViewSearchAdapter.addFooterLoading()
            isLoading = false
        }
    }

    override fun onDestroy() {
        presenter.apply {
            if (isConnected) {
                unbindService(serviceConnection)
                isConnected = false
            }
            unregisterReceiver(localReceiver)
        }
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    override fun onItemClick(song: Song) {
        presenter.handleStartSong(song)
    }

    private inner class SendBroadcast {
        fun sendPendingIntent(action: String) {
            val intent = Intent(Constant.ACTION_MUSIC_BROADCAST)
            intent.putExtra(Constant.ACTION_MUSIC, action)
            PendingIntent.getBroadcast(
                applicationContext,
                Random.nextInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            ).send()
        }
    }
}

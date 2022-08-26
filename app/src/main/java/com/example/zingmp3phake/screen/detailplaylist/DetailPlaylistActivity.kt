package com.example.zingmp3phake.screen.detailplaylist

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.core.net.toUri
import com.example.zingmp3phake.R
import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.databinding.ActivityDetailPlaylistBinding
import com.example.zingmp3phake.screen.detailsong.DetailSongActivity
import com.example.zingmp3phake.screen.personal.RecyclerViewRecentAdapter
import com.example.zingmp3phake.utils.Constant
import com.example.zingmp3phake.utils.MusicAction
import com.example.zingmp3phake.utils.NetworkUtils
import com.example.zingmp3phake.utils.TIME_DELAY_FOR_LOAD
import com.example.zingmp3phake.utils.base.BaseActivity
import com.example.zingmp3phake.utils.handler
import com.example.zingmp3phake.utils.loadByGlide
import kotlin.random.Random

class DetailPlaylistActivity :
    BaseActivity<ActivityDetailPlaylistBinding>(ActivityDetailPlaylistBinding::inflate),
    RecyclerViewRecentAdapter.ItemClickListener,
    DetailPlaylistContract.View {

    private lateinit var adapterDetailPlaylist: RecyclerViewRecentAdapter
    private lateinit var presenter: DetailPlaylistPresenter
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
        val bundle = intent.getBundleExtra(Constant.DATA_KEY)
        val title = bundle?.getString(Constant.BUNDLE_TITLE_STRING_KEY)
        val list = bundle?.getParcelableArrayList<Song>(Constant.BUNDLE_LIST_KEY)
        binding.textNumberOfListSong.text = "${list?.size} ${Constant.SONG}"
        binding.recyclerViewPlaylist.adapter = adapterDetailPlaylist
        if (list != null) adapterDetailPlaylist.setData(list)
        if (title != null) binding.textPlaylistName.text = title
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
        }
    }

    override fun initView() {
        adapterDetailPlaylist = RecyclerViewRecentAdapter(this)
        presenter = DetailPlaylistPresenter()
        presenter.setView(this)
    }

    override fun onItemClick(pos: Int, listSong: MutableList<Song>) {
        val song = listSong.get(pos)
        if (song.isLocal == false && NetworkUtils.isNetworkAvailable(applicationContext) == false) {
            Toast.makeText(applicationContext, "${Constant.NO_INTERNET}", Toast.LENGTH_SHORT).show()
            return
        }
        presenter.handleStartSong(listSong, pos)
        binding.buttonPlay.setImageResource(R.drawable.ic_play_24)
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

    override fun onRestart() {
        super.onRestart()
        presenter.handlePlayOrPauseSong()
    }

    override fun onDestroy() {
        presenter.apply {
            unBindService(applicationContext)
            unregisterReceiver(localReceiver)
        }
        super.onDestroy()
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

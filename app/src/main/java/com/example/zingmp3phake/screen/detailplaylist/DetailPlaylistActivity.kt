package com.example.zingmp3phake.screen.detailplaylist

import android.app.PendingIntent
import android.content.ContentUris
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.view.View
import androidx.core.net.toUri
import com.example.zingmp3phake.R
import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.databinding.ActivityDetailPlaylistBinding
import com.example.zingmp3phake.screen.detailsong.DetailSongActivity
import com.example.zingmp3phake.screen.personal.RecyclerViewRecentAdapter
import com.example.zingmp3phake.utils.ACTION_MUSIC
import com.example.zingmp3phake.utils.ACTION_MUSIC_BROADCAST
import com.example.zingmp3phake.utils.BUNDLE_LIST_KEY
import com.example.zingmp3phake.utils.BUNDLE_TITLE_STRING_KEY
import com.example.zingmp3phake.utils.DATA_KEY
import com.example.zingmp3phake.utils.MEDIA_EXTERNAL_AUDIO_URI
import com.example.zingmp3phake.utils.MusicAction
import com.example.zingmp3phake.utils.SONG
import com.example.zingmp3phake.utils.base.BaseActivity
import com.example.zingmp3phake.utils.loadByGlide
import kotlin.random.Random

class DetailPlaylistActivity :
    BaseActivity<ActivityDetailPlaylistBinding>(ActivityDetailPlaylistBinding::inflate),
    RecyclerViewRecentAdapter.ItemClickListener,
    DetailPlaylistContract.View {

    private lateinit var adapterDetailPlaylist: RecyclerViewRecentAdapter
    private lateinit var presenter: DetailPlaylistPresenter

    override fun initData() {
        val bundle = intent.getBundleExtra(DATA_KEY)
        val title = bundle?.getString(BUNDLE_TITLE_STRING_KEY)
        val list = bundle?.getParcelableArrayList<Song>(BUNDLE_LIST_KEY)
        binding.textNumberOfListSong.text = "${list?.size} $SONG"
        binding.recyclerViewPlaylist.adapter = adapterDetailPlaylist
        if (list != null) adapterDetailPlaylist.setData(list)
        if (title != null) binding.textPlaylistName.text = title
    }

    override fun handleEvent() {
        presenter.apply {
            bindService(applicationContext)
            val filter = IntentFilter(ACTION_MUSIC_BROADCAST)
            registerReceiver(localReceiver, filter)
        }
        binding.apply {
            buttonBack.setOnClickListener {
                finish()
            }
            buttonNext.setOnClickListener {
                getPendingIntent(MusicAction.NEXT.name)?.send()
                binding.buttonPlay.setImageResource(R.drawable.ic_play_24)
            }
            buttonPlay.setOnClickListener {
                getPendingIntent(MusicAction.PLAYORPAUSE.name)?.send()
            }
            buttonFavorite.setOnClickListener {
                getPendingIntent(MusicAction.FAVORITE.name)?.send()
            }
            containerStateSong.setOnClickListener {
                val intent = Intent(applicationContext, DetailSongActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun initView() {
        adapterDetailPlaylist = RecyclerViewRecentAdapter(this)
        presenter = DetailPlaylistPresenter(this)
    }

    override fun onItemClick(pos: Int, listSong: MutableList<Song>) {
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
                    Uri.parse(MEDIA_EXTERNAL_AUDIO_URI),
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

    override fun onDestroy() {
        presenter.apply {
            unBindService(applicationContext)
            unregisterReceiver(localReceiver)
        }
        super.onDestroy()
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
}

package com.example.zingmp3phake.screen.detailsong

import com.example.zingmp3phake.data.repo.SongRepository
import com.example.zingmp3phake.data.repo.resource.Listener
import com.example.zingmp3phake.screen.MusicService
import com.example.zingmp3phake.utils.ApiConstant
import com.example.zingmp3phake.utils.Constant
import java.net.URL

class DetailSongPresenter(
    val songRepo: SongRepository
) :
    DetailSongContract.Presenter {

    private var view: DetailSongContract.View? = null
    var musicService = MusicService()

    fun handlePlayOrPause() {
        view?.displayPlayOrPause(musicService.isPlayings)
    }

    override fun getCurrentSong() {
        val song = musicService.listSongs.get(musicService.positions)
        view?.displayCurrentSong(song)
    }

    override fun handleChangeSeekBar(value: Int) {
        musicService.onChangeSeekBar(value)
    }

    override fun getLyrics() {
        val song = musicService.listSongs.get(musicService.positions)
        if (song.lyrics.equals(Constant.N0_LYRIC)) view?.displayLyricSong(
            mutableListOf(
                Constant.N0_LYRIC
            )
        )
        else {
            val url = URL("${ApiConstant.API_BASE_SPOTIFY}track_lyrics/?id=${song.songInfo.songid}")
            songRepo.remote.getLyricSong(
                url,
                object : Listener<MutableList<String>> {
                    override fun onSuccess(list: MutableList<String>) {
                        view?.displayLyricSong(list)
                    }

                    override fun onFail(msg: String) {
                        view?.displayLyricSong(mutableListOf(Constant.N0_LYRIC))
                    }
                }
            )
        }
    }

    override fun handleEventFavorite() {
        view?.displayPlayOrPause(musicService.isPlayings)
        val song = musicService.listSongs.get(musicService.positions)
        view?.displayFavorite(song.isFavorite)
    }

    override fun onStart() {
        // TODO("Not yet implemented")
    }

    override fun onStop() {
        // TODO("Not yet implemented")
    }

    override fun setView(view: DetailSongContract.View?) {
        this.view = view
    }
}

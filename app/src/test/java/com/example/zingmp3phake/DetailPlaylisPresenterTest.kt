package com.example.zingmp3phake

import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.data.model.SongInfo
import com.example.zingmp3phake.screen.MusicService
import com.example.zingmp3phake.screen.detailplaylist.DetailPlaylistContract
import com.example.zingmp3phake.screen.detailplaylist.DetailPlaylistPresenter
import com.example.zingmp3phake.utils.Constant
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class DetailPlaylisPresenterTest {
    private val view = mockk<DetailPlaylistContract.View>(relaxed = true)
    private val presenter = DetailPlaylistPresenter().apply { setView(view) }
    private val musicService = mockk<MusicService>()

    @Before
    fun prepare() {
        presenter.musicService = musicService
    }

    @Test
    fun `getCurrentSong with empty list`() {
        every {
            musicService.listSongs
        } returns mutableListOf()

        every {
            musicService.positions
        } returns 0
        every {
            musicService.isPlayings
        } returns false
        presenter.getCurrentSong()
        verify {
            view.displayCurrentSong(null)
        }
    }

    @Test
    fun `getCurrentSong with list`() {
        val songInfo = SongInfo(
            "", "", "", 100,
            "/storage/emulated/0/Download/Khong Chi La Thich Nhac Chuong " +
                    "- Ton Ngu Trai Tieu Toan (NhacPro.net).mp3",
            "sdgfdg"
        )
        val song = Song(songInfo, true, true, Constant.N0_LYRIC)
        val songs = mutableListOf(song)
        every {
            musicService.listSongs
        } returns songs

        every {
            musicService.positions
        } returns 0

        every {
            musicService.isPlayings
        } returns false

        presenter.getCurrentSong()

        verify {
            view.displayCurrentSong(song)
        }
    }

    @Test
    fun `handlePlaySong`() {
        every {
            musicService.isPlayings
        } returns false
        presenter.handlePlayOrPauseSong()
        verify {
            view.displayPlayOrPause(false)
        }
    }

    @Test
    fun `handleFavoriteSong`() {
        val song = Song(SongInfo(), false, false, "")
        val songs = mutableListOf(song)
        every {
            musicService.listSongs
        } returns songs
        every {
            musicService.positions
        } returns 0
        presenter.handleFavorite()
        verify {
            view.displayFavorite(false)
        }
    }

    @Test
    fun `handleStartSong`() {
        val songInfo = SongInfo(
            "", "", "", 100,
            "/storage/emulated/0/Download/Khong Chi La Thich Nhac Chuong " +
                    "- Ton Ngu Trai Tieu Toan (NhacPro.net).mp3",
            "sdgfdg"
        )
        val song = Song(songInfo, true, true, Constant.N0_LYRIC)
        every {
            musicService.startSong(mutableListOf(song), 0)
        } returns Unit
        presenter.handleStartSong(mutableListOf(song), 0)
        verify {
            musicService.startSong(mutableListOf(song), 0)
        }
    }
}

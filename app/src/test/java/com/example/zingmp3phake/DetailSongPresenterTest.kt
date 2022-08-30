package com.example.zingmp3phake

import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.data.model.SongInfo
import com.example.zingmp3phake.data.repo.SongRepository
import com.example.zingmp3phake.data.repo.resource.Listener
import com.example.zingmp3phake.screen.MusicService
import com.example.zingmp3phake.screen.detailsong.DetailSongContract
import com.example.zingmp3phake.screen.detailsong.DetailSongPresenter
import com.example.zingmp3phake.utils.Constant
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import java.net.URL

class DetailSongPresenterTest {
    private val view = mockk<DetailSongContract.View>(relaxed = true)
    private val repository = mockk<SongRepository>()
    private val presenter = DetailSongPresenter(repository).apply { setView(view) }
    private val musicService = mockk<MusicService>()
    private val callback = slot<Listener<MutableList<String>>>()

    @Before
    fun prepare() {
        presenter.musicService = musicService
    }

    @Test
    fun `getCurrentSong`() {
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
        presenter.handlePlayOrPause()
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
        every {
            musicService.isPlayings
        } returns true
        presenter.handleEventFavorite()
        verify {
            view.displayFavorite(false)
        }
    }

    @Test
    fun `handleChangeSeekbar`() {
        every {
            musicService.onChangeSeekBar(100000)
        } returns Unit
        presenter.handleChangeSeekBar(100000)
        verify {
            musicService.onChangeSeekBar(100000)
        }
    }

    @Test
    fun `getLyric with song no lyric`() {
        val song = Song(SongInfo(), false, false, Constant.N0_LYRIC)
        val songs = mutableListOf(song)
        every {
            musicService.listSongs
        } returns songs
        every {
            musicService.positions
        } returns 0
        every {
            musicService.isPlayings
        } returns true
        presenter.getLyrics()
        verify {
            view.displayLyricSong(mutableListOf(Constant.N0_LYRIC))
        }
    }

    @Test
    fun `getLyric success`() {
        val song = Song(SongInfo(), false, false, "")
        val songs = mutableListOf(song)
        val lyric = mutableListOf("a", "b", "c")
        every {
            musicService.listSongs
        } returns songs
        every {
            musicService.positions
        } returns 0
        every {
            musicService.isPlayings
        } returns true
        every {
            repository.remote.getLyricSong(
                URL("https://spotify23.p.rapidapi.com/track_lyrics/?id=null"),
                capture(callback)
            )
        } answers {
            callback.captured.onSuccess(lyric)
        }
        presenter.getLyrics()
        verify {
            view.displayLyricSong(lyric)
        }
    }

    @Test
    fun `getLyricSong fail`() {
        val song = Song(SongInfo(), false, false, "")
        val songs = mutableListOf(song)
        val lyric = mutableListOf("a", "b", "c")
        every {
            musicService.listSongs
        } returns songs
        every {
            musicService.positions
        } returns 0
        every {
            musicService.isPlayings
        } returns true
        every {
            repository.remote.getLyricSong(
                URL("https://spotify23.p.rapidapi.com/track_lyrics/?id=null"),
                capture(callback)
            )
        } answers {
            callback.captured.onFail(Constant.N0_LYRIC)
        }
        presenter.getLyrics()
        verify {
            view.displayLyricSong(mutableListOf(Constant.N0_LYRIC))
        }
    }
}

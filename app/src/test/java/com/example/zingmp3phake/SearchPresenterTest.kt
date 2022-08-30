package com.example.zingmp3phake

import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.data.model.SongInfo
import com.example.zingmp3phake.data.repo.SongRepository
import com.example.zingmp3phake.data.repo.resource.Listener
import com.example.zingmp3phake.screen.MusicService
import com.example.zingmp3phake.screen.search.SearchContract
import com.example.zingmp3phake.screen.search.SearchPresenter
import com.example.zingmp3phake.utils.Constant
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import java.net.URL

class SearchPresenterTest {
    private val view = mockk<SearchContract.View>(relaxed = true)
    private val songRepository = mockk<SongRepository>()
    private val presenter = SearchPresenter(songRepository).apply { setView(view) }
    private val callback = slot<Listener<MutableList<Song>>>()
    private var musicService = mockk<MusicService>()

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
            view.displayPlayOrPause(false)
        }
    }

    @Test
    fun `getFristResultSearch callback success`() {
        val song = Song(SongInfo(), false, false, "")
        val songs = mutableListOf(song)
        every {
            songRepository.remote.getResultSearchSong(
                URL("https://spotify23.p.rapidapi.com/search/?q=&type=tracks&limit=10"),
                capture(callback)
            )
        } answers {
            callback.captured.onSuccess(songs)
        }
        presenter.getFisrtResultSearch("")
        verify {
            view.displayResultSearch(songs)
        }
    }

    @Test
    fun `getFisrtResultSearch callback fail`() {
        val song = Song(SongInfo(), false, false, "")
        val songs = mutableListOf(song)
        every {
            songRepository.remote.getResultSearchSong(
                URL("https://spotify23.p.rapidapi.com/search/?q=&type=tracks&limit=10"),
                capture(callback)
            )
        } answers {
            callback.captured.onFail(Constant.NO_DATA)
        }
        presenter.getFisrtResultSearch("")
        verify {
            view.displayResultSearch(mutableListOf())
        }
    }

    @Test
    fun `getMoreResultSearch callback success`() {
        val song = Song(SongInfo(), false, false, "")
        val songs = mutableListOf(song)
        every {
            songRepository.remote.getResultSearchSong(
                URL("https://spotify23.p.rapidapi.com/search/?q=null&type=tracks&offset=0&limit=10"),
                capture(callback)
            )
        } answers {
            callback.captured.onSuccess(songs)
        }
        presenter.getMoreResultSearch()
        verify {
            view.displayResultSearch(songs)
        }
    }

    @Test
    fun `getMoreResultSearch callback fail`() {
        val song = Song(SongInfo(), false, false, "")
        val songs = mutableListOf(song)
        every {
            songRepository.remote.getResultSearchSong(
                URL("https://spotify23.p.rapidapi.com/search/?q=null&type=tracks&offset=0&limit=10"),
                capture(callback)
            )
        } answers {
            callback.captured.onFail(Constant.NO_DATA)
        }
        presenter.getMoreResultSearch()
        verify {
            view.displayResultSearch(mutableListOf())
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
        presenter.handleStartSong(song)
        verify {
            musicService.startSong(mutableListOf(song), 0)
        }
    }
}

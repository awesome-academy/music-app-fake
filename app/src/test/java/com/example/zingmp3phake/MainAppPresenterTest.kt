package com.example.zingmp3phake

import android.content.Context
import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.data.model.SongInfo
import com.example.zingmp3phake.data.repo.SongRepository
import com.example.zingmp3phake.data.repo.resource.local.LocalSong
import com.example.zingmp3phake.screen.MusicService
import com.example.zingmp3phake.screen.mainapp.MainAppPresenter
import com.example.zingmp3phake.screen.mainapp.MainContract
import com.example.zingmp3phake.utils.Constant
import com.example.zingmp3phake.utils.NetworkUtils
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

class MainAppPresenterTest {
    private val view = mockk<MainContract.View>(relaxed = true)
    private val repository = mockk<SongRepository>()
    private val presenter = MainAppPresenter(repository).apply { setView(view) }
    private val musicService = mockk<MusicService>()
    private val context = mock<Context>()
    private val network = mockk<NetworkUtils>()
    private val local = mockk<LocalSong>()

    @Before
    fun prepare() {
        presenter.musicService = musicService
    }

    @Test
    fun `handleNextSong `() {
        val song = Song(SongInfo(), true, false, "")
        every {
            musicService.listSongs
        } returns mutableListOf(song)
        every {
            musicService.positions
        } returns 0
        every {
            musicService.startSong(mutableListOf(song), 0)
        } returns Unit
        presenter.handleNextSong(context)
        verify {
            musicService.startSong(mutableListOf(song), 0)
        }
    }

    @Test
    fun `handleNextSong without internet`() {
        val song = Song(SongInfo(), false, false, "")
        every {
            musicService.listSongs
        } returns mutableListOf(song)
        every {
            musicService.positions
        } returns 0
        every {
            network.isNetworkAvailable(context)
        } returns false
        presenter.handleNextSong(context)
        verify {
            view.displayNoInternet()
        }
    }

    @Test
    fun `handlePlayingSong`() {
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
        } returns mutableListOf(song)
        every {
            musicService.positions
        } returns 0
        every {
            musicService.isPlayings
        } returns (false)
        every {
            musicService.playOrPause()
        } returns Unit
        presenter.handlePlayOrPauseSong(context)
        verify {
            view.onPlaySong()
            musicService.playOrPause()
        }
    }

    @Test
    fun `handlePauseSong`() {
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
        } returns mutableListOf(song)
        every {
            musicService.positions
        } returns 0
        every {
            musicService.isPlayings
        } returns (true)
        every {
            musicService.playOrPause()
        } returns Unit
        presenter.handlePlayOrPauseSong(context)
        verify {
            view.onPauseSong()
        }
    }

    @Test
    fun `handleFavoriteSong`() {
        val song = Song(SongInfo(), true, false, "")
        every {
            musicService.listSongs
        } returns mutableListOf(song)
        every {
            musicService.positions
        } returns 0
        presenter.handleFavoriteSong(context)
        every {
            repository.addSongFavorite(song)
        } returns Unit
        verify {
            view.displayFavotite()
        }
    }

    @Test
    fun `handleUnFavoriteSong`() {
        val song = Song(SongInfo(), false, false, "")
        musicService.listSongs.clear()
        musicService.listSongs.add(song)
        musicService.positions = 0
        presenter.handleFavoriteSong(context)
        verify {
            view.displayUnFavorite()
            repository.addSongFavorite(song)
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
        val songs = mutableListOf(song)
        every {
            musicService.listSongs
        } returns mutableListOf(song)
        every {
            musicService.positions
        } returns 0
        every {
            musicService.isPlayings
        } returns (false)
        every {
            musicService.startSong(songs, 0)
        } returns Unit
        every {
            musicService.playOrPause()
        } returns Unit
        presenter.handlePlayOrPauseSong(context)
        verify {
            view.onPlaySong()
        }
        presenter.handleStartSong(context)
        verify {
            view.onStartSong(song)
        }
    }

    @Test
    fun `handleStartSong with NoInternet`() {
        val song = Song(SongInfo(), false, false, "")
        every {
            musicService.listSongs
        } returns mutableListOf(song)
        every {
            musicService.positions
        } returns 0
        every {
            network.isNetworkAvailable(context)
        } returns false
        presenter.handleStartSong(context)
        verify {
            view.displayNoInternet()
        }
    }

    @Test
    fun `handlePreviousSong`() {
        val song = Song(SongInfo(), true, false, "")
        every {
            musicService.listSongs
        } returns mutableListOf(song)
        every {
            musicService.positions
        } returns 0
        every {
            musicService.startSong(mutableListOf(song), 0)
        } returns Unit
        presenter.handlePreviousSong(context)
        verify {
            musicService.startSong(mutableListOf(song), 0)
        }
    }

    @Test
    fun `handlePreviousSong with NoInternet`() {
        val song = Song(SongInfo(), false, false, "")
        every {
            musicService.listSongs
        } returns mutableListOf(song)
        every {
            musicService.positions
        } returns 0
        every {
            network.isNetworkAvailable(context)
        } returns false
        presenter.handlePreviousSong(context)
        verify {
            view.displayNoInternet()
        }
    }

    @Test
    fun `handlePreviousSong with position greater than 0`() {
        val song = Song(SongInfo(), true, false, "")
        every {
            musicService.listSongs
        } returns mutableListOf(song)
        every {
            musicService.positions
        } returns 1
        every {
            musicService.startSong(mutableListOf(song), 0)
        } returns Unit
        presenter.handlePreviousSong(context)
        verify {
            musicService.startSong(mutableListOf(song), 0)
        }
    }
}

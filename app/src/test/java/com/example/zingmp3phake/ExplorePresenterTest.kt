package com.example.zingmp3phake

import android.content.Context
import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.data.model.SongInfo
import com.example.zingmp3phake.data.repo.SongRepository
import com.example.zingmp3phake.data.repo.resource.Listener
import com.example.zingmp3phake.screen.MusicService
import com.example.zingmp3phake.screen.explore.ExploreContract
import com.example.zingmp3phake.screen.explore.ExplorePresenter
import com.example.zingmp3phake.utils.Constant
import com.example.zingmp3phake.utils.NetworkUtils
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Test
import org.mockito.kotlin.mock

class ExplorePresenterTest {
    private val view = mockk<ExploreContract.View>(relaxed = true)
    private val repository = mockk<SongRepository>()
    private val presenter = ExplorePresenter(repository).apply { setView(view) }
    private val callback = slot<Listener<MutableList<Song>>>()
    private val context = mock<Context>()
    private val musicService = MusicService()

    @Test
    fun `getTrendingSong callback return success`() {
        val song = Song(SongInfo(), false, false, Constant.N0_LYRIC)
        val songs = mutableListOf(song)
        every {
            repository.getTrendingSong(capture(callback))
        } answers {
            callback.captured.onSuccess(songs)
        }
        presenter.getTrendingSong(context)
        verify(exactly = 1) {
            view.displaySuccess(songs)
        }
    }

    @Test
    fun `getTrendingSong callback return fail`() {
        every {
            repository.getTrendingSong(capture(callback))
        } answers {
            callback.captured.onFail(Constant.NO_DATA)
        }
        presenter.getTrendingSong(context)
        verify {
            view.displayFail(Constant.NO_DATA)
        }
    }

    @Test
    fun `handleStartSong notLocal and noConnected Internet`() {
        val song = Song(SongInfo(), false, true, Constant.N0_LYRIC)
        val songs = mutableListOf(song)
        val network = mockk<NetworkUtils>()
        every {
            network.isNetworkAvailable(context)
        } returns false
        presenter.handlerStartSong(songs, 0, context)
        verify {
            view.displayNoInternet()
        }
    }

    @Test
    fun `handleStartSong local`() {
        val songInfo = SongInfo(
            "", "", "", 100,
            "/storage/emulated/0/Download/Khong Chi La Thich Nhac Chuong " +
                    "- Ton Ngu Trai Tieu Toan (NhacPro.net).mp3",
            "sdgfdg"
        )
        val song = Song(songInfo, true, true, Constant.N0_LYRIC)
        val songs = mutableListOf(song)
        presenter.handlerStartSong(songs, 0, context)
        verify {
            musicService.startSong(songs, 0)
        }
    }
}

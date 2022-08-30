package com.example.zingmp3phake

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.data.model.SongInfo
import com.example.zingmp3phake.data.repo.SongRepository
import com.example.zingmp3phake.data.repo.resource.Listener
import com.example.zingmp3phake.data.repo.resource.local.LocalSong
import com.example.zingmp3phake.data.repo.resource.remote.RemoteSong
import com.example.zingmp3phake.screen.MusicService
import com.example.zingmp3phake.screen.personal.PerSonalPresenter
import com.example.zingmp3phake.screen.personal.PersonalContract
import com.example.zingmp3phake.utils.Constant
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class PersonalPresenterTest {
    private val view = mockk<PersonalContract.View>(relaxed = true)
    private val repository = mockk<SongRepository>()
    private val presenter = PerSonalPresenter(repository).apply { setView(view) }
    private val callback = slot<Listener<MutableList<Song>>>()
    private val context = mockk<Context>()
    private val appCompatActivity = mockk<AppCompatActivity>()
    private val musicService = MusicService()
    private val local = mockk<LocalSong>()

    @Before
    fun prepare() {
        presenter.musicService = musicService
    }

    @Test
    fun `getLocalSong callback return success`() {
        val song = Song(SongInfo(), false, false, Constant.N0_LYRIC)
        val songs = mutableListOf(song)
        every {
            repository.getSongLocal(context, capture(callback))
        } answers {
            callback.captured.onSuccess(songs)
        }
        every {
            appCompatActivity.applicationContext
        } returns (context)

        every {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        } returns PackageManager.PERMISSION_GRANTED

        every {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE
            )
        } returns PackageManager.PERMISSION_GRANTED

        presenter.getLocalSong(appCompatActivity)
        verify(exactly = 1) {
            view.getLocalSongSuccess(songs)
        }
    }

    @Test
    fun `getLocalSong callback return fail`() {
        every {
            repository.getSongLocal(context, capture(callback))
        } answers {
            callback.captured.onFail(Constant.NO_DATA)
        }
        every {
            appCompatActivity.applicationContext
        } returns (context)

        every {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        } returns PackageManager.PERMISSION_GRANTED

        every {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE
            )
        } returns PackageManager.PERMISSION_GRANTED
        presenter.getLocalSong(appCompatActivity)
        presenter.requestReadPermission(appCompatActivity)
        verify(exactly = 1) {
            view.getSongFail(Constant.NO_DATA)
        }
    }

    @Test
    fun `getLocalSong without Permission`() {
        every {
            appCompatActivity.applicationContext
        } returns (context)

        every {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        } returns -1

        every {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE
            )
        } returns -1
        presenter.getLocalSong(appCompatActivity)
    }

    @Test
    fun `getFavoriteSong callback return success`() {
        val song = Song(SongInfo(), false, false, Constant.N0_LYRIC)
        val songs = mutableListOf(song)
        every {
            repository.getSongFavorite(capture(callback))
        } answers {
            callback.captured.onSuccess(songs)
        }
        presenter.getFavoriteSong(context)
        verify(exactly = 1) {
            view.getFavoriteSongSuccess(songs)
        }
    }

    @Test
    fun `getFavoriteSong callback return fail`() {
        every {
            repository.getSongFavorite(capture(callback))
        } answers {
            callback.captured.onFail(Constant.NO_DATA)
        }
        presenter.getFavoriteSong(context)
        verify(exactly = 1) {
            view.getSongFail(Constant.NO_DATA)
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
        presenter.handleStartSong(songs, 0, context)
        verify {
            musicService.startSong(songs, 0)
        }
    }

    @Test
    fun `addSongRecent`() {
        val song = Song(SongInfo(), false, false, Constant.N0_LYRIC)
        val songs = mutableListOf(song)
        every {
            repository.local
        } returns local
        every {
            repository.local.addSongRecent(song)
        } returns Unit
        presenter.musicService.listSongs.add(song)
        presenter.addSongRecent()
    }

    @Test
    fun `getRecentSong suceess`() {
        val song = Song(SongInfo(), false, false, Constant.N0_LYRIC)
        val songs = mutableListOf(song)
        every {
            repository.getSongRecent(context,capture(callback))
        } answers {
            callback.captured.onSuccess(songs)
        }
        presenter.getRecentSong(context)
        verify {
            view.getRecentSong(songs)
        }
    }

    @Test
    fun `getRecentSong fail`() {
        every {
            repository.getSongRecent(context,capture(callback))
        } answers {
            callback.captured.onFail(Constant.NO_DATA)
        }
        presenter.getRecentSong(context)
        verify {
            view.getSongFail(Constant.NO_DATA)
        }
    }
}

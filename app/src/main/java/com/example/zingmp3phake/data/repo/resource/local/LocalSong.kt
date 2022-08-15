package com.example.zingmp3phake.data.repo.resource.local

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.provider.MediaStore
import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.data.model.SongInfo
import com.example.zingmp3phake.data.repo.resource.Listener
import com.example.zingmp3phake.data.repo.resource.SongDataSource
import com.example.zingmp3phake.data.repo.resource.local.database.SongDatabase
import com.example.zingmp3phake.utils.INDEX_0
import com.example.zingmp3phake.utils.INDEX_1
import com.example.zingmp3phake.utils.INDEX_2
import com.example.zingmp3phake.utils.INDEX_3
import com.example.zingmp3phake.utils.INDEX_4
import com.example.zingmp3phake.utils.INDEX_5
import com.example.zingmp3phake.utils.INDEX_6
import com.example.zingmp3phake.utils.INDEX_7
import com.example.zingmp3phake.utils.INDEX_8
import com.example.zingmp3phake.utils.N0_LYRIC
import com.example.zingmp3phake.utils.NO_DATA
import com.example.zingmp3phake.utils.TABLE_SONG
import com.example.zingmp3phake.utils.handler
import java.util.concurrent.Executors
import java.util.logging.Logger

class LocalSong : SongDataSource.SongLocalSource {

    private val executor = Executors.newSingleThreadExecutor()
    private var context: Context? = null
    override fun getSongLocal(context: Context?, listen: Listener<MutableList<Song>>) {
        this.context = context
        val mRunnable = object : Runnable {
            override fun run() {
                handleGetSonglocal(context, listen)
            }
        }
        executor.execute(mRunnable)
    }

    private fun handleGetSonglocal(context: Context?, listen: Listener<MutableList<Song>>) {
        val list = mutableListOf<Song>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"
        val cursor = context?.contentResolver?.query(uri, null, selection, null, null)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val url = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
                val name = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
                val artist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
                val duration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
                val img = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
                val id = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
                if (url != null) list.add(
                    Song(
                        SongInfo(
                            cursor.getString(id),
                            cursor.getString(name),
                            cursor.getString(artist),
                            duration,
                            cursor.getString(url),
                            cursor.getString(img)
                        ),
                        true,
                        false,
                        mutableListOf(N0_LYRIC)
                    )
                )
            }
        }
        executor.execute {
            handler.post({
                if (list.size > 0) listen.onSuccess(list)
                else listen.onFail(NO_DATA)
            })
        }
    }

    override fun getSongRecent(context: Context?, listen: Listener<MutableList<Song>>) {
        val listSong = mutableListOf<Song>()
        executor.execute {
            val sql = "SELECT * FROM $TABLE_SONG "
            val data = SongDatabase.getInstance(context).getData(sql)
            while (data.moveToNext()) {
                data.also {
                    val id = it.getString(INDEX_0)
                    val name = it.getString(INDEX_1)
                    val artist = it.getString(INDEX_2)
                    val duration = it.getInt(INDEX_3)
                    val url = it.getString(INDEX_4)
                    val img = it.getString(INDEX_5)
                    val local = if (it.getInt(INDEX_6) == 1) true else false
                    val favorite = if (it.getInt(INDEX_7) == 1) true else false
                    val lyric = it.getString(INDEX_8)
                    listSong.add(
                        Song(
                            SongInfo(id, name, artist, duration, url, img),
                            local,
                            favorite,
                            mutableListOf(lyric)
                        )
                    )
                }
            }
            executor.execute {
                handler.post {
                    if (listSong.size == 0) {
                        listen.onFail(NO_DATA)
                    } else listen.onSuccess(listSong)
                }
            }
        }
    }

    override fun getSongFavorite(listen: Listener<MutableList<Song>>) {
        val listSong = mutableListOf<Song>()
        executor.execute {
            val sql = "SELECT * FROM $TABLE_SONG WHERE ${Song.SONG_FAVORITE} =1"
            val data = SongDatabase.getInstance(context).getData(sql)
            while (data.moveToNext()) {
                data.also {
                    val id = it.getString(INDEX_0)
                    val name = it.getString(INDEX_1)
                    val artist = it.getString(INDEX_2)
                    val duration = it.getInt(INDEX_3)
                    val url = it.getString(INDEX_4)
                    val img = it.getString(INDEX_5)
                    val local = if (it.getInt(INDEX_6) == 1) true else false
                    val favorite = if (it.getInt(INDEX_7) == 1) true else false
                    val lyric = it.getString(INDEX_8)
                    listSong.add(
                        Song(
                            SongInfo(id, name, artist, duration, url, img),
                            local,
                            favorite,
                            mutableListOf(lyric)
                        )
                    )
                }
            }
            executor.execute {
                handler.post {
                    if (listSong.size == 0) {
                        listen.onFail(NO_DATA)
                    } else listen.onSuccess(listSong)
                }
            }
        }
    }

    override fun addSongRecent(song: Song) {
        executor.execute {
            try {
                val isLocal = if (song.isLocal) 1 else 0
                val isfavorite = if (song.isFavorite) 1 else 0
                val sql =
                    "INSERT INTO $TABLE_SONG VALUES ('${song.songInfo.songid}', '${song.songInfo.songName}', " +
                        "'${song.songInfo.songArtist}', ${song.songInfo.duration}, '${song.songInfo.songUrl}', " +
                        "'${song.songInfo.songImg}', $isLocal, $isfavorite, '${song.lyrics}');"
                SongDatabase.getInstance(context).queryData(sql)
            } catch (e: SQLiteConstraintException) {
                Logger.getLogger(e.toString())
            }
        }
    }

    override fun addSongFavorite(song: Song) {
        executor.execute {
            val sql =
                "update $TABLE_SONG set ${Song.SONG_FAVORITE} = 1 where ${Song.SONG_ID} = '${song.songInfo.songid}';"
            SongDatabase.getInstance(context).queryData(sql)
        }
    }

    override fun removeSongFavorite(song: Song) {
        executor.execute {
            val sql =
                "update $TABLE_SONG set ${Song.SONG_FAVORITE} = 0 where ${Song.SONG_ID} = '${song.songInfo.songid}';"
            SongDatabase.getInstance(context).queryData(sql)
        }
    }

    override fun getSong(id: String): Song? {
        var song: Song? = null
        executor.execute {
            val sql = "SELECT * FROM $TABLE_SONG WHERE ${Song.SONG_FAVORITE} =1;"
            val data = SongDatabase.getInstance(context).getData(sql)
            while (data.moveToNext()) {
                data.also {
                    val id = it.getString(INDEX_0)
                    val name = it.getString(INDEX_1)
                    val artist = it.getString(INDEX_2)
                    val duration = it.getInt(INDEX_3)
                    val url = it.getString(INDEX_4)
                    val img = it.getString(INDEX_5)
                    val local = if (it.getInt(INDEX_6) == 1) true else false
                    val favorite = if (it.getInt(INDEX_7) == 1) true else false
                    val lyric = it.getString(INDEX_8)
                    song = Song(
                        SongInfo(id, name, artist, duration, url, img),
                        local,
                        favorite,
                        mutableListOf(lyric)
                    )
                }
            }
        }
        return song
    }

    companion object {
        private var instance: LocalSong? = null
        fun getInstance() = synchronized(this) {
            instance ?: LocalSong().also { instance = it }
        }
    }
}

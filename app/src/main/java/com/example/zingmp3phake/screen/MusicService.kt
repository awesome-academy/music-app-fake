package com.example.zingmp3phake.screen

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.zingmp3phake.R
import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.utils.ACTION_MUSIC
import com.example.zingmp3phake.utils.ACTION_MUSIC_BROADCAST
import com.example.zingmp3phake.utils.CHANNEL_ID
import com.example.zingmp3phake.utils.CHANNEL_NAME
import com.example.zingmp3phake.utils.MEDIA_EXTERNAL_AUDIO_URI
import com.example.zingmp3phake.utils.MusicAction
import com.example.zingmp3phake.utils.NOTIFICATION_ID
import com.example.zingmp3phake.utils.TAG_MEDIA_SESSION
import com.example.zingmp3phake.utils.TITILE_NEXT
import com.example.zingmp3phake.utils.TITILE_PAUSE
import com.example.zingmp3phake.utils.TITILE_PLAY
import java.lang.reflect.InvocationTargetException
import java.util.logging.Logger
import kotlin.random.Random

class MusicService : Service() {

    var listSongs = mutableListOf<Song>()
    var positions = 0
    var isPlayings = false
    private var mediaPlayer = MediaPlayer()
    private val binder = LocalBinder()
    private var isCreate = false
    private var notification: NotificationCompat.Builder? = null

    inner class LocalBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    private fun sendNotifications(song: Song) {
        notification?.apply {
            setContentTitle(song.songInfo.songName)
            setContentText(song.songInfo.songArtist)
            clearActions()
            if (isPlayings) addAction(
                R.drawable.ic_play_24,
                TITILE_PLAY,
                getPendingIntent(MusicAction.PLAYORPAUSE.name)
            )
            else addAction(
                R.drawable.ic_pause_24,
                TITILE_PAUSE,
                getPendingIntent(MusicAction.PLAYORPAUSE.name)
            )
            addAction(
                R.drawable.ic_next_24,
                TITILE_NEXT,
                getPendingIntent(MusicAction.NEXT.name)
            )
        }
        var bitmap: Bitmap? = null
        if (listSongs.get(positions).isLocal) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    bitmap = ImageDecoder.decodeBitmap(
                        ImageDecoder.createSource(
                            this.contentResolver,
                            ContentUris.withAppendedId(
                                Uri.parse(MEDIA_EXTERNAL_AUDIO_URI),
                                song.songInfo.songImg.toLong()
                            )
                        )
                    )
                }
            } catch (e: InvocationTargetException) {
                Logger.getLogger(e.toString())
                bitmap = BitmapFactory.decodeResource(this.resources, R.drawable.imgzingmp3logo)
            }
            notification?.setLargeIcon(bitmap)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForeground(NOTIFICATION_ID, notification?.build())
            } else {
                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(NOTIFICATION_ID, notification?.build())
            }
        } else {
            Glide.with(applicationContext).asBitmap()
                .load(listSongs.get(positions).songInfo.songImg)
                .into(object : CustomTarget<Bitmap?>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap?>?
                    ) {
                        notification?.setLargeIcon(resource)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForeground(NOTIFICATION_ID, notification?.build())
                        } else {
                            val notificationManager =
                                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                            notificationManager.notify(NOTIFICATION_ID, notification?.build())
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // TODO later
                    }
                })
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
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

    fun playOrPause() {
        if (isPlayings) {
            mediaPlayer?.pause()
            isPlayings = false
            sendNotifications(listSongs.get(positions))
        } else {
            mediaPlayer?.start()
            isPlayings = true
            sendNotifications(listSongs.get(positions))
        }
    }

    fun onChangeSeekBar(value: Int) {
        mediaPlayer?.seekTo(value)
    }

    fun startSong(listSong: MutableList<Song>, pos: Int) {
        this.listSongs = listSong
        positions = pos
        isPlayings = true
        getPendingIntent(MusicAction.START.name)?.send()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
        if (listSong.get(pos).isLocal) {
            mediaPlayer =
                MediaPlayer.create(
                    applicationContext,
                    listSong.get(positions).songInfo.songUrl.toUri()
                )
        } else {
            mediaPlayer = MediaPlayer()
            mediaPlayer.apply {
                setAudioStreamType(AudioManager.STREAM_MUSIC)
                setDataSource(listSong.get(pos).songInfo.songUrl)
                prepare()
                start()
            }
        }
        mediaPlayer?.apply {
            isLooping = false
            setOnCompletionListener {
                getPendingIntent(MusicAction.NEXT.name)?.send()
            }
            start()
        }
        if (isCreate == false) {
            createNotifi()
        }
        sendNotifications(listSong.get(positions))
    }

    private fun createNotifi() {
        isCreate = true
        val mediaSession = MediaSessionCompat(this, TAG_MEDIA_SESSION)
        notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .apply {
                setSmallIcon(R.drawable.ic_favorite_24)
                setStyle(
                    androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1)
                        .setMediaSession(mediaSession.sessionToken)
                )
                if (isPlayings) addAction(
                    R.drawable.ic_play_24,
                    TITILE_PLAY,
                    getPendingIntent(MusicAction.PLAYORPAUSE.name)
                )
                else addAction(
                    R.drawable.ic_pause_24,
                    TITILE_PAUSE,
                    getPendingIntent(MusicAction.PLAYORPAUSE.name)
                )
                addAction(
                    R.drawable.ic_next_24,
                    TITILE_NEXT,
                    getPendingIntent(MusicAction.NEXT.name)
                )
            }
        createNotificationChannel()
    }

    fun getCurrentSongTime(): Int? = mediaPlayer?.currentPosition
}

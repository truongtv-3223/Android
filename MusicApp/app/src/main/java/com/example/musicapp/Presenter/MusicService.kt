package com.example.musicapp.Presenter

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
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.example.musicapp.R
import com.example.musicapp.data.model.Song
import com.example.musicapp.utils.*
import kotlin.random.Random


class MusicService : Service() {
    private var listSong = mutableListOf<Song>()
    private var positon = 0
    private var isPlaying = false
    private var mediaPlayer: MediaPlayer? = null
    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }


    private fun sendNotifications(song: Song) {

        createNotificationChannel()

        val imgSong = ContentUris.withAppendedId(
            Uri.parse(MEDIA_EXTERNAL_AUDIO_URI),
            song.albumUri.toLong()
        )
        var bitmap: Bitmap? = null
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                bitmap = ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(
                        this.contentResolver,
                        imgSong
                    )
                )
            }
        } catch (e: Exception) {
            bitmap = BitmapFactory.decodeResource(this.resources, R.drawable.song)
        }
        var mediaSession = MediaSessionCompat(this, TAG_MEDIA_SESSION)
        var notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .apply {
                setSmallIcon(R.drawable.ic_headset)
                setLargeIcon(bitmap)
                setContentTitle(song.name)
                setContentText(song.author)
                setStyle(
                    androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2)
                        .setMediaSession(mediaSession.sessionToken)
                )
                addAction(R.drawable.ic_pre, TITILE_PRE, getPendingIntent(PREVIOUS_SONG))
                if (isPlaying) addAction(
                    R.drawable.ic_play,
                    TITILE_PLAY,
                    getPendingIntent(PLAY_OR_PAUSE_SONG)
                )
                else addAction(R.drawable.ic_pauses, TITILE_PAUSE, getPendingIntent(PLAY_OR_PAUSE_SONG))
                addAction(R.drawable.ic_next, TITILE_NEXT, getPendingIntent(NEXT_SONG))
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFICATION_ID, notification.build())
        } else {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, notification.build())
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
        var intent = Intent(ACTION_MUSIC_BROADCAST)
        intent.putExtra(ACTION_MUSIC, action)
        return PendingIntent.getBroadcast(
            applicationContext,
            Random.nextInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    fun playOrPause() {
        if (isPlaying) {
            mediaPlayer?.pause()
            isPlaying = false
            sendNotifications(listSong.get(positon))
        } else {
            mediaPlayer?.start()
            isPlaying = true
            sendNotifications(listSong.get(positon))
        }
    }


    fun onChangeSeekBar(value: Int) {
        mediaPlayer?.seekTo(value)
    }

    fun playSong(listSong: MutableList<Song>, pos: Int, nextSong: () -> Unit) {
        this.listSong = listSong

        positon = pos
        isPlaying = true
        mediaPlayer?.stop()
        mediaPlayer?.release()
        Thread.sleep(TIME_SLEEP)
        mediaPlayer = MediaPlayer.create(applicationContext, listSong.get(positon).uri.toUri())
        mediaPlayer?.apply {
            isLooping = false
            setOnCompletionListener {
                nextSong()
            }
            start()
        }
        sendNotifications(listSong.get(pos))
    }

    fun getCurrentSongTime(): Int? = mediaPlayer?.currentPosition

    fun isPlaying(): Boolean = isPlaying
}

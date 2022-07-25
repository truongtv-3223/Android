package com.example.musicapp.Presenter

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.musicapp.MainActivity
import com.example.musicapp.R
import com.example.musicapp.data.model.Song
import com.example.musicapp.utils.*


class MusicService : Service() {
    var listSong = mutableListOf<Song>()
    var positon =0
    var isPlaying=false
    var mediaPlayer : MediaPlayer? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var bundle = intent?.extras
            listSong=bundle?.getSerializable("listSong") as MutableList<Song>
            positon = bundle.getInt("pos",0)
        handleAction(intent?.action)
        sendNotification(listSong.get(positon))
        return START_NOT_STICKY
    }

    private fun sendNotification(song: Song) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var notificationChannel = NotificationChannel(CHANNEL_ID,"Music",NotificationManager.IMPORTANCE_DEFAULT)
            var notificationManager : NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
        var intent = Intent(this, MainActivity::class.java)
        var pendingIntent = PendingIntent.getActivity(this, 0,intent, PendingIntent.FLAG_UPDATE_CURRENT)
        var notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(song.name)
            .setContentText(song.author)
            .setSmallIcon(R.drawable.ic_headset)
            .build()
        startForeground(1, notification)
    }

    private fun handleAction(action: String?) {
        when(action){
            NEXT_SONG-> nextSong()
            PLAY_OR_PAUSE_SONG -> playOrPause()
            PREVIOUS_SONG ->preSong()
            START_SONG ->playSong()
        }
    }

    fun nextSong() {
         playSong()
    }

    fun playOrPause(){
        if(isPlaying) {
            mediaPlayer?.pause()
            isPlaying = false
        }
        else    {
            mediaPlayer?.start()
            isPlaying = true
        }
    }

    fun preSong(){
        playSong()
    }

    fun onChangeSeekBar(value : Int){
        mediaPlayer?.seekTo(value)
    }

    fun playSong(){
        isPlaying =true
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(applicationContext,listSong.get(positon).uri.toUri() )
        mediaPlayer?.apply {
            isLooping = false
            setOnCompletionListener {
                nextSong()
            }
            start()
        }
    }
}
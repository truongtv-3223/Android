package com.example.musicapp.Presenter

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
import android.content.pm.PackageManager
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.viewbinding.ViewBinding
import com.example.musicapp.data.model.Song
import com.example.musicapp.data.repo.SongRepo
import com.example.musicapp.data.repo.resource.local.ResultSourceListener
import com.example.musicapp.databinding.ActivityMainBinding
import com.example.musicapp.positon
import com.example.musicapp.utils.*
import java.io.Serializable
import java.util.logging.Filter
import kotlin.math.log


class PlayMusicPresenter(val playMusicView : IPlayMusic.View,
                         val songRepo : SongRepo): IPlayMusic.Presenter {
    private lateinit var musicService: MusicService
    private lateinit var context: Context
    private lateinit var listSong: MutableList<Song>
    private var isConnection = false
    private var handler = Handler(Looper.getMainLooper())
    private var serviceConnection = object : ServiceConnection{
        override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.LocalBinder
            musicService = binder.getService()
            isConnection = true
            musicService.playSong(listSong, positon){
                handleNextSong()
            }
            getTimeforView()
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isConnection = false
        }

    }

    private var localReciver = object : BroadcastReceiver(){

        override fun onReceive(p0: Context?, intent: Intent?) {
            when(intent?.getStringExtra(ACTION_MUSIC)){
                PLAY_OR_PAUSE_SONG -> handlePlaySong()
                NEXT_SONG -> handleNextSong()
                PREVIOUS_SONG -> handlePreviousSong()
            }
        }

    }


    override fun getSong(activity: AppCompatActivity) {
        context=activity.applicationContext
        if(checkReadPermission(activity.applicationContext)){
            requestReadPermission(activity)
        }else{
            songRepo.getSongLocalSource(object :ResultSourceListener<MutableList<Song>>{
                override fun onSuccess(list: MutableList<Song>) {
                    playMusicView.getSongSuccess(list)
                    listSong = list
                }
                override fun onFail(mess: String) {
                    playMusicView.getSongFail(mess)
                }

            })
        }
    }

    override fun handlePlaySong() {
        if(musicService.isPlaying()){
            playMusicView.onPauseSong()
        }else playMusicView.onPlaySong()
        musicService.playOrPause()
    }

    override fun handleStartSong(pos : Int) {
        if(isConnection){
            musicService.playSong(listSong, positon){
                handleNextSong()
            }
        }else{
            var service = Intent(context,MusicService::class.java)
            context.bindService(service,serviceConnection,Context.BIND_AUTO_CREATE)
            registerBroadcast()
        }

    }



    private fun getTimeforView() {
        handler.post(object : Runnable{
            override fun run() {
                var time =  musicService?.getCurrentSongTime()
                if (time != null) {
                    playMusicView.disPlayCurrentSongTime(time)
                }else playMusicView.disPlayCurrentSongTime(0)
                handler.removeCallbacks(this)
                handler.postDelayed(this, TIME_SLEEP_100)
            }
        })
    }

    override fun handleNextSong() {
        positon = (positon+1) % listSong.size
        playMusicView.onStartSong(positon)
        musicService.playSong(listSong, positon){
            handleNextSong()
        }
    }

    override fun handlePreviousSong() {
        positon = if(positon-1>=0) positon-1
        else listSong.size-1
        playMusicView.onStartSong(positon)
        musicService.playSong(listSong, positon){
            handleNextSong()
        }
    }

    override fun handleChangSeekBar(value: Int) {
        musicService.onChangeSeekBar(value)
    }

    override fun stopMusicService() {
        if(isConnection){
            context.unbindService(serviceConnection)
            isConnection = false
        }
    }

    override fun registerBroadcast() {
        var filter = IntentFilter(ACTION_MUSIC_BROADCAST)
        context.registerReceiver(localReciver,filter)
    }

    override fun unRegisterBroadcast() {
        context.unregisterReceiver(localReciver)
    }


    private fun checkReadPermission(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
    }

    private fun requestReadPermission(activity : AppCompatActivity){
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            READ_PERMISSION_REQUEST_CODE
        )
    }
}

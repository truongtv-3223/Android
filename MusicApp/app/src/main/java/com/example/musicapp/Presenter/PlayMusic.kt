package com.example.musicapp.Presenter

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.musicapp.data.model.Song
import com.example.musicapp.data.repo.SongRepo
import com.example.musicapp.data.repo.resource.local.ResultSourceListener
import com.example.musicapp.positon
import com.example.musicapp.utils.*
import java.io.Serializable


class PlayMusic(val playMusicView : IPlayMusic.View,
                val songRepo : SongRepo): IPlayMusic.Presenter {
    lateinit var musicService: MusicService
    lateinit var context: Context
    lateinit var listSong: MutableList<Song>
    private var isPlaying=false
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
        var service = Intent(context, MusicService::class.java)
        var bundle = Bundle()
        bundle.putSerializable("listSong",listSong as Serializable)
        service.action = PLAY_OR_PAUSE_SONG
        service.putExtras(bundle)
        context.startService(service)
        if(isPlaying) {
            playMusicView.onPauseSong()
            isPlaying = false
        }
        else {
            playMusicView.onPlaySong()
            isPlaying = true
        }
    }

    override fun handleStartSong(pos : Int) {
        isPlaying=true
        var service = Intent(context,MusicService::class.java)
        var bundle =Bundle()
        bundle.putInt("pos", positon)
        bundle.putSerializable("listSong", com.example.musicapp.listSong as Serializable?)
        service.action= START_SONG
        service.putExtras(bundle)
        context.startService(service)
    }

    override fun handleNextSong() {
        positon = (positon+1) % listSong.size
        var service = Intent(context,MusicService::class.java)
        var bundle =Bundle()
        bundle.putInt("pos", positon)
        bundle.putSerializable("listSong", com.example.musicapp.listSong as Serializable?)
        service.action= NEXT_SONG
        service.putExtras(bundle)
        context.startService(service)
    }

    override fun handlePreviousSong() {
        positon = if(positon-1>=0) positon
        else listSong.size-1
        var service = Intent(context,MusicService::class.java)
        var bundle =Bundle()
        bundle.putInt("pos", positon)
        bundle.putSerializable("listSong", com.example.musicapp.listSong as Serializable?)
        service.action= PREVIOUS_SONG
        service.putExtras(bundle)
        context.startService(service)
    }

    override fun handleChangSeekBar(value: Int) {

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
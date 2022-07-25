package com.example.musicapp.Presenter

import androidx.appcompat.app.AppCompatActivity
import com.example.musicapp.data.model.Song

interface IPlayMusic {
    interface View{
        fun getSongSuccess(list : MutableList<Song>)
        fun getSongFail(m : String)
        fun onStartSong(pos : Int)
        fun onPauseSong()
        fun onPlaySong()
    }
    interface Presenter{
        fun getSong(activity : AppCompatActivity)
        fun handlePlaySong()
        fun handleNextSong()
        fun handleStartSong(pos : Int)
        fun handlePreviousSong()
        fun handleChangSeekBar(value : Int)
    }
}
package com.example.musicapp.Presenter

import com.example.musicapp.data.model.Song
import com.example.musicapp.data.model.State

class PlayMusic(val playMusicInterface: IPlayMusic) {
    fun play(){
        if(State.stateSong) playMusicInterface.pause()
        else playMusicInterface.play()
    }
    fun previous(){
        playMusicInterface.previous<Song>()
    }
    fun next(){
        playMusicInterface.next<Song>()
    }
    fun loadSong(){
        playMusicInterface.loadSong<MutableList<Song>>()
    }
}
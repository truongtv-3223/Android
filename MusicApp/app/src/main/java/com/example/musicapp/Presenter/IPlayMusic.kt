package com.example.musicapp.Presenter

interface IPlayMusic {
    fun play()
    fun pause()
    fun <T>next() : T
    fun <T>previous() : T
    fun <T>loadSong() : T
}
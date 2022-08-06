package com.example.musicapp.data.repo

import com.example.musicapp.data.model.Song
import com.example.musicapp.data.repo.resource.SongDataSource
import com.example.musicapp.data.repo.resource.local.ResultSourceListener

class SongRepo private constructor(val local : SongDataSource.SongLocalSource) : SongDataSource.SongLocalSource{
    override fun getSongLocalSource(listener: ResultSourceListener<MutableList<Song>>) {
        local.getSongLocalSource(listener)
    }
    companion object{
        private var instance : SongRepo?=null
        fun getInstance(local: SongDataSource.SongLocalSource) =
            synchronized(this){
                instance ?: SongRepo(local).also { instance=it }
            }
    }
}

package com.example.musicapp.data.repo.resource.local

import com.example.musicapp.data.model.Song
import com.example.musicapp.data.repo.resource.SongDataSource

class LocalSong : SongDataSource.SongLocalSource {
    override fun getSongLocalSource(listener: ResultSourceListener<MutableList<Song>>) {
        var list = mutableListOf<Song>()
        //

        //
        if(list.size==0) listener.onFail("No data")
        listener.onSuccess(list)
    }
    companion object{
        private var instance : LocalSong?=null
        fun getInstance()= synchronized(this){
                instance ?:LocalSong().also { instance=it }
            }
    }
}
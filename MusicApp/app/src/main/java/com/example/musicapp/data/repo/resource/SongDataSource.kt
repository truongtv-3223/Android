package com.example.musicapp.data.repo.resource

import com.example.musicapp.data.model.Song
import com.example.musicapp.data.repo.resource.local.ResultSourceListener

interface SongDataSource {
    interface SongLocalSource{
       fun getSongLocalSource(listener: ResultSourceListener<MutableList<Song>>)
    }
}
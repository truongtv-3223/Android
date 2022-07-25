package com.example.musicapp.data.repo.resource.local

import android.content.Context
import android.provider.MediaStore
import com.example.musicapp.data.model.Song
import com.example.musicapp.data.repo.resource.SongDataSource

class LocalSong(private val context: Context) : SongDataSource.SongLocalSource {
    override fun getSongLocalSource(listener: ResultSourceListener<MutableList<Song>>) {
        var list = mutableListOf<Song>()
        //
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"
        val cursor = context.contentResolver.query(uri,null,selection,null,null)
        if (cursor != null){
            while(cursor.moveToNext()){
                with(cursor){
                    val url = if (getColumnIndex(MediaStore.Audio.Media.DATA)<0) ""
                             else cursor.getString(getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                    val id =if (getColumnIndex(MediaStore.Audio.Media._ID)<0) ""
                             else getString(getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                    val title =if (getColumnIndex(MediaStore.Audio.Media.TITLE)<0) ""
                                else getString(getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                    val author = if (getColumnIndex(MediaStore.Audio.Media.ARTIST)<0) ""
                                else getString(getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                    val albumUri = if (cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)<0) ""
                                    else getString(getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
                    val time = if (cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)<0) 0
                                else getInt(getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                    if (url!="") {
                        list.add(Song(id,title,author,albumUri,url,time))
                    }
                }
            }
        }
        //
        if(list.size==0) listener.onFail("No data")
        listener.onSuccess(list)
    }


    companion object{
        private var instance : LocalSong?=null
        fun getInstance(context : Context)= synchronized(this){
                instance ?:LocalSong(context).also { instance=it }
            }
    }
}
package com.example.musicapp.data.model

import android.os.Parcelable
import java.io.Serializable

 class Song(var id: String,
           var name: String,
           var author: String,
           var albumUri: String,
           var uri: String,
           var time: Int){
}

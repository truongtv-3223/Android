package com.example.musicapp.data.repo.resource.local

interface ResultSourceListener <T>{
    fun onSuccess(list : T)
    fun onFail(mess : String)
}
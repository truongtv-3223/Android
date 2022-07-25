package com.example.musicapp.data.repo.resource.local


//lưu kết quả để trả về cho Client
interface ResultSourceListener <T>{
    fun onSuccess(list : T)
    fun onFail(mess : String)
}
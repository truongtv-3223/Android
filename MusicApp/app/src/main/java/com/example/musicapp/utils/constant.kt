package com.example.musicapp.utils

import java.util.concurrent.TimeUnit


const val READ_PERMISSION_REQUEST_CODE = 123
const val MEDIA_EXTERNAL_AUDIO_URI= "content://media/external/audio/albumart"
const val NEXT_SONG= "action.next.song"
const val PREVIOUS_SONG="action.previous.song"
const val PLAY_OR_PAUSE_SONG="action.play.or.pause.song"
const val START_SONG ="action.start.song"
const val ACTION_MUSIC = "action.music"
const val ACTION_MUSIC_BROADCAST = "action.music.broadcast"
const val CHANNEL_ID ="chanel_id_notification"


class constant {
    companion object{
         fun getTimetoSecond(second : Int): String{
            var minute = TimeUnit.MILLISECONDS.toMinutes(second.toLong())
            var second = TimeUnit.MILLISECONDS.toSeconds(second.toLong())%60
            var time = String.format("%02d:%02d",minute,second)
            return time

        }
        fun getSecondtoMilisecond(ml : Int) : Int{
            return TimeUnit.MILLISECONDS.toSeconds(ml.toLong()).toInt()
        }
    }
}

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
const val CHANNEL_NAME = "Music"
const val START_TIME = "00:00"
const val START_PROCESS_SEEK_BAR =0
const val NOTIFICATION_ID =1
const val TITILE_PRE ="previous"
const val TITILE_NEXT ="next"
const val TITILE_PLAY ="play"
const val TITILE_PAUSE="pause"
const val TAG_MEDIA_SESSION = "tag"
const val INTENT_LIST ="listsong"
const val INTENT_POS = "pos"
const val TIME_SLEEP = 500L
const val TIME_SLEEP_50 = 50L
const val TIME_SLEEP_100 = 100L

class constant {
    companion object{
         fun getTimetoSecond(second : Int): String{
            var minute = TimeUnit.MILLISECONDS.toMinutes(second.toLong())
            var second = TimeUnit.MILLISECONDS.toSeconds(second.toLong())%60
            var time = String.format("%02d:%02d",minute,second)
            return time

        }
    }
}

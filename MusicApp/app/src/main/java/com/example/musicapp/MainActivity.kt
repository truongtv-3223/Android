package com.example.musicapp

import android.app.NotificationManager
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicapp.Presenter.IPlayMusic
import com.example.musicapp.Presenter.MusicService
import com.example.musicapp.Presenter.PlayMusic
import com.example.musicapp.adapter.AdapterRecycleView
import com.example.musicapp.data.model.Song
import com.example.musicapp.data.repo.SongRepo
import com.example.musicapp.data.repo.resource.local.LocalSong
import com.example.musicapp.utils.MEDIA_EXTERNAL_AUDIO_URI
import com.example.musicapp.utils.START_SONG
import de.hdodenhof.circleimageview.CircleImageView
import java.io.Serializable
import java.util.concurrent.TimeUnit

lateinit var recycleView : RecyclerView
lateinit var adapterRv : AdapterRecycleView
lateinit var constraintLayout: ConstraintLayout
lateinit var circleImageView: CircleImageView
lateinit var imgPlay : ImageButton
lateinit var outCurrentTimeSong : TextView
lateinit var outEndTimeSong : TextView
lateinit var outSongName: TextView
lateinit var outAuthorName : TextView
lateinit var imgNext : ImageButton
lateinit var imgPre : ImageButton
lateinit var listSong : MutableList<Song>
lateinit var seekBar: SeekBar

lateinit var mHandler : Handler
lateinit var  monitor : Runnable
var positon=0;
var  stateRotate =0
class MainActivity : AppCompatActivity(), IPlayMusic.View,AdapterRecycleView.ItemClickListener{

    private val mPlayMusicPresenter =PlayMusic(this,SongRepo.getInstance(LocalSong.getInstance(this)))
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initComponent()
        imgPlay.setOnClickListener(View.OnClickListener {
            mPlayMusicPresenter.handlePlaySong()
        })
        imgPre.setOnClickListener(View.OnClickListener {
            mPlayMusicPresenter.handlePreviousSong()
            imgPlay.setImageResource(R.drawable.ic_pause)
        })

        imgNext.setOnClickListener(View.OnClickListener {
            mPlayMusicPresenter.handleNextSong()
            imgPlay.setImageResource(R.drawable.ic_pause)
        })

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
           override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

           }

           override fun onStartTrackingTouch(p0: SeekBar?) {

           }

           override fun onStopTrackingTouch(p0: SeekBar?) {
                mPlayMusicPresenter.handleChangSeekBar(p0!!.progress)
           }

       })

    }

    private fun initComponent() {
        constraintLayout = findViewById(R.id.constraintlayout)
        recycleView = findViewById(R.id.rv)
        circleImageView = findViewById(R.id.cimg)
        imgPlay = findViewById(R.id.btnPlay)
        imgNext = findViewById(R.id.btnNext)
        imgPre = findViewById(R.id.btnPre)
        seekBar = findViewById(R.id.sb)
        outCurrentTimeSong = findViewById(R.id.outCurrentTime)
        outEndTimeSong = findViewById(R.id.outEndTime)
        outSongName = findViewById(R.id.outSongName)
        outAuthorName = findViewById(R.id.outSongAuthor)
        listSong = mutableListOf<Song>()
        adapterRv = AdapterRecycleView(this)
        var layoutManager = LinearLayoutManager(this)
        recycleView.adapter= adapterRv
        recycleView.layoutManager = layoutManager
        mPlayMusicPresenter.getSong(this)
        mHandler = Handler()
        monitor = Runnable{}
    }

    override fun getSongSuccess(list: MutableList<Song>) {
        listSong = list
        adapterRv.setData(list)
        for( x in list){
            Log.d("AAAAAA", x.uri)
        }
    }

    override fun getSongFail(m: String) {
        Toast.makeText(applicationContext,m , Toast.LENGTH_LONG).show()
    }

    override fun onPlaySong() {
        imgPlay.setImageResource(R.drawable.ic_pause)
        startAnim()
    }
    override fun onStartSong(pos: Int) {
        positon=pos
        stateRotate=0
        val imgSong = ContentUris.withAppendedId(
            Uri.parse(MEDIA_EXTERNAL_AUDIO_URI),
            listSong.get(pos).albumUri.toLong()
        )
        Glide.with(applicationContext)
            .load(imgSong)
            .placeholder(resources.getDrawable(R.drawable.head))
            .into(circleImageView)
        startAnim()
        constraintLayout.visibility = View.VISIBLE
        imgPlay.setImageResource(R.drawable.ic_pause)

        outEndTimeSong.setText(getTimetoSecond(listSong.get(pos).time))
        outAuthorName.setText(listSong.get(pos).author)
        outSongName.setText(listSong.get(pos).name)
        seekBar.max= listSong.get(pos).time
    }


    override fun onPauseSong() {
        imgPlay.setImageResource(R.drawable.ic_play)
        mHandler.removeCallbacks(monitor)
    }

    override fun onItemClick(pos: Int) {
        onStartSong(pos)
        mPlayMusicPresenter.handleStartSong(pos)
    }


    private fun startAnim() {
        mHandler.post(monitor)
        mHandler.removeCallbacks(monitor)
         monitor= object : Runnable{
             override fun run() {
                 circleImageView.rotation= stateRotate++.toFloat()
                 mHandler.postDelayed(this, 50)
                 }
             }
         mHandler.post(monitor)
     }

    private fun getTimetoSecond(second : Int): String{
        var minute = TimeUnit.MILLISECONDS.toMinutes(second.toLong())
        var second = TimeUnit.MILLISECONDS.toSeconds(second.toLong())%60
        var time = String.format("%02d:%02d",minute,second)
        return time

    }


}
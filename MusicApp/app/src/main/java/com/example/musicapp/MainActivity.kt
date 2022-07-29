package com.example.musicapp

import android.content.ContentUris
import android.net.Uri
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicapp.Presenter.IPlayMusic
import com.example.musicapp.Presenter.PlayMusicPresenter
import com.example.musicapp.adapter.AdapterRecycleView
import com.example.musicapp.data.model.Song
import com.example.musicapp.data.repo.SongRepo
import com.example.musicapp.data.repo.resource.local.LocalSong
import com.example.musicapp.databinding.ActivityMainBinding
import com.example.musicapp.utils.*
import de.hdodenhof.circleimageview.CircleImageView
import java.util.concurrent.TimeUnit


var positon = 0;
class MainActivity : AppCompatActivity(), IPlayMusic.View,AdapterRecycleView.ItemClickListener{

    private lateinit var adapterRv : AdapterRecycleView
    private lateinit var viewBinding : ActivityMainBinding
    private lateinit var listSong : MutableList<Song>

    var mHandler = Handler()
    var monitor = Runnable{}
    var  stateRotate = 0
    private val mPlayMusicPresenter =PlayMusicPresenter(this,SongRepo.getInstance(LocalSong.getInstance(this)))
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initComponent()
        viewBinding.apply {
            recyleview.adapter = adapterRv
            btnPlay.setOnClickListener(View.OnClickListener {
                mPlayMusicPresenter.handlePlaySong()
            })
            btnPre.setOnClickListener(View.OnClickListener {
                mPlayMusicPresenter.handlePreviousSong()
                btnPlay.setImageResource(R.drawable.ic_play)
            })

            btnNext.setOnClickListener(View.OnClickListener {
                mPlayMusicPresenter.handleNextSong()
                btnPlay.setImageResource(R.drawable.ic_play)
            })

            seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    if (p0 != null) {
                        mPlayMusicPresenter.handleChangSeekBar(p0.progress)
                    }
                }

            })
        }

    }

    private fun initComponent() {
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        listSong = mutableListOf<Song>()
        adapterRv = AdapterRecycleView(this)

        mPlayMusicPresenter.getSong(this)

    }

    override fun getSongSuccess(list: MutableList<Song>) {
        listSong = list
        adapterRv.setData(list)
    }

    override fun getSongFail(m: String) {
        Toast.makeText(applicationContext,m , Toast.LENGTH_LONG).show()
    }

    override fun onPlaySong() {
        viewBinding.btnPlay.setImageResource(R.drawable.ic_play)
        startAnim()
    }

    override fun disPlayCurrentSongTime(time: Int) {
        viewBinding.outCurrentTime.text = constant.getTimetoSecond(time)
        viewBinding.seekbar.progress = time
    }

    override fun onStartSong(pos: Int) {
        positon = pos
        stateRotate = 0
        val imgSong = ContentUris.withAppendedId(
            Uri.parse(MEDIA_EXTERNAL_AUDIO_URI),
            listSong.get(pos).albumUri.toLong()
        )
        Glide.with(applicationContext)
            .load(imgSong)
            .placeholder(resources.getDrawable(R.drawable.head))
            .into(viewBinding.cimg)
        startAnim()
        viewBinding.apply {
            constraintlayout.visibility = View.VISIBLE
            btnPlay.setImageResource(R.drawable.ic_play)

            outEndTime.text = constant.getTimetoSecond(listSong.get(pos).time)
            outSongAuthor.text = listSong.get(pos).author
            outSongName.text = listSong.get(pos).name
            seekbar.max = listSong.get(pos).time
            outCurrentTime.text = START_TIME
            seekbar.progress = START_PROCESS_SEEK_BAR
        }
    }


    override fun onPauseSong() {
        viewBinding.btnPlay.setImageResource(R.drawable.ic_pauses)
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
                 viewBinding.cimg.rotation= stateRotate++.toFloat()
                 mHandler.postDelayed(this, TIME_SLEEP_50)
                 }
             }
         mHandler.post(monitor)
     }



    override fun onDestroy() {
        super.onDestroy()
        mPlayMusicPresenter.stopMusicService()
        mPlayMusicPresenter.unRegisterBroadcast()
    }
}

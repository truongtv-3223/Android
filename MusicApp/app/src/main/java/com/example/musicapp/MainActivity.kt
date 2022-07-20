package com.example.musicapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.Presenter.PlayMusic
import com.example.musicapp.adapter.AdapterRecycleView
import com.example.musicapp.data.model.Song
import com.example.musicapp.data.model.State
import de.hdodenhof.circleimageview.CircleImageView

lateinit var recycleView : RecyclerView
lateinit var adapterRv : AdapterRecycleView
lateinit var constraintLayout: ConstraintLayout
lateinit var circleImageView: CircleImageView
lateinit var imgPlay : ImageButton
lateinit var mHandler : Handler
lateinit var  monitor : Runnable
lateinit var outCurrentTime: TextView
lateinit var imgNext : ImageButton
lateinit var imgPre : ImageButton
var  stateRotate =0
private lateinit var playMusic : PlayMusic
class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initComponent()

        imgPlay.setOnClickListener(View.OnClickListener {
            playMusic.play()
        })
    }

    private fun initComponent() {
        var song = Song(R.drawable.song,"Phía sau một cô gái","Soobin Hoàng Sơn",0)
        var song1 = Song(R.drawable.head,"","",0)
        var listSong : MutableList<Song> = mutableListOf(song,song,song,song,song,song,song,song,song1)
        adapterRv = AdapterRecycleView(listSong)
        var layoutManager = LinearLayoutManager(this)
        recycleView.adapter= adapterRv
        recycleView.layoutManager = layoutManager
        constraintLayout = findViewById(R.id.constraintlayout)
        recycleView = findViewById(R.id.rv)
        circleImageView = findViewById(R.id.cimg)
        imgPlay = findViewById(R.id.btnPlay)
        imgNext = findViewById(R.id.btnNext)
        imgPre = findViewById(R.id.btnPre)
        outCurrentTime = findViewById(R.id.outStartTime)
    //    playMusic= PlayMusic(this)
        constraintLayout.visibility = View.VISIBLE
        playMusic.loadSong()
    }

    private fun startAnim() {
     //   circleImageView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_spin))
        mHandler = Handler()
         monitor= object : Runnable{
            override fun run() {
                circleImageView.rotation= stateRotate++.toFloat()
                mHandler.postDelayed(this, 50)
                if(stateRotate%20==0){
                    State.currentSongTime++
                    var mi : String = (State.currentSongTime/60).toString()
                    var se : String = (State.currentSongTime%60).toString()
                    if(mi.length==1) mi="0"+mi
                    if(se.length==1) se="0"+se
                    outCurrentTime.setText("$mi:$se")
                }

            }
        }
        mHandler.post(monitor)
    }

}
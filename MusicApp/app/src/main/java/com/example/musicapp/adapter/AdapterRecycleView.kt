package com.example.musicapp.adapter

import android.content.ContentUris
import android.content.Context

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicapp.R
import com.example.musicapp.data.model.Song
import com.example.musicapp.utils.MEDIA_EXTERNAL_AUDIO_URI

class AdapterRecycleView(val listener : ItemClickListener) : RecyclerView.Adapter<AdapterRecycleView.ViewHolder?>() {

    private var mContext : Context? =null
    private var listSong = mutableListOf<Song>()
    fun setData(listSong: MutableList<Song>){
        this.listSong = listSong
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        mContext = parent.context
        val view = inflater.inflate(R.layout.item_recycle_view, parent, false)
        return ViewHolder(view,listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.author.setText(listSong.get(position).author)
        holder.name.setText(listSong.get(position).name)
        val imgSong = ContentUris.withAppendedId(
            Uri.parse(MEDIA_EXTERNAL_AUDIO_URI),
            listSong.get(position).albumUri.toLong()
        )
        mContext?.let {
            Glide.with(it)
                .load(imgSong)
                .placeholder(R.drawable.song)
                .into(holder.img)
        }
    }

    override fun getItemCount(): Int {
        if(listSong==null) return 0
        return listSong.size
    }

    class ViewHolder(itemView: View, listener : ItemClickListener) : RecyclerView.ViewHolder(itemView) {
        lateinit var img : ImageView
        lateinit var name : TextView
        lateinit var author : TextView
        init {
            img = itemView.findViewById(R.id.imgSong)
            name = itemView.findViewById(R.id.outSongName)
            author = itemView.findViewById(R.id.outSongAuthor)
            itemView.setOnClickListener(View.OnClickListener {
                listener.onItemClick(adapterPosition)
            })
        }

    }

    interface ItemClickListener{
        fun onItemClick(pos : Int)
    }
}
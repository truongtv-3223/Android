package com.example.musicapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.data.model.Song

class AdapterRecycleView(var listSong : MutableList<Song>) : RecyclerView.Adapter<AdapterRecycleView.ViewHolder?>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_recycle_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.img.setImageResource(listSong.get(position).img)
        holder.author.setText(listSong.get(position).author)
        holder.name.setText(listSong.get(position).name)
    }

    override fun getItemCount(): Int {
        if(listSong==null) return 0
        return listSong.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var img : ImageView
        lateinit var name : TextView
        lateinit var author : TextView
        init {
            img = itemView.findViewById(R.id.imgSong)
            name = itemView.findViewById(R.id.outSongName)
            author = itemView.findViewById(R.id.outSongAuthor)
        }
    }
}
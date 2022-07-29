package com.example.musicapp.adapter

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicapp.R
import com.example.musicapp.data.model.Song
import com.example.musicapp.databinding.ItemRecycleViewBinding
import com.example.musicapp.utils.MEDIA_EXTERNAL_AUDIO_URI

class AdapterRecycleView(private val listener: ItemClickListener) :
    RecyclerView.Adapter<AdapterRecycleView.ViewHolder?>() {

    private lateinit var mContext: Context
    private val listSong = mutableListOf<Song>()
    fun setData(listSong: List<Song>) {
        this.listSong.apply {
            clear()
            addAll(listSong)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        mContext = parent.context
        val viewBinding = ItemRecycleViewBinding.inflate(inflater)
        return ViewHolder(viewBinding, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.viewBinding.apply {
            outSongAuthor.text = listSong.get(position).author
            outSongName.text = listSong.get(position).name
        }
        val imgSong = ContentUris.withAppendedId(
            Uri.parse(MEDIA_EXTERNAL_AUDIO_URI),
            listSong.get(position).albumUri.toLong()
        )
        mContext?.let {
            Glide.with(it)
                .load(imgSong)
                .placeholder(R.drawable.song)
                .into(holder.viewBinding.imgSong)
        }
    }

    override fun getItemCount(): Int {
        return listSong.size
    }

    class ViewHolder(var viewBinding: ItemRecycleViewBinding, listener: ItemClickListener) :
        RecyclerView.ViewHolder(viewBinding.root) {

        init {
            itemView.setOnClickListener(View.OnClickListener {
                listener.onItemClick(adapterPosition)
            })
        }

    }

    interface ItemClickListener {
        fun onItemClick(pos: Int)
    }
}

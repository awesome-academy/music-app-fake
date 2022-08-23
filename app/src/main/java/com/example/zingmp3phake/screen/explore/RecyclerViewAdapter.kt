package com.example.zingmp3phake.screen.explore

import android.content.ContentUris
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.databinding.ItemRecycleViewBinding
import com.example.zingmp3phake.utils.Constant
import com.example.zingmp3phake.utils.loadByGlide

class RecyclerViewAdapter(private val listener: ItemClickListener) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder?>() {

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
        val viewBinding = ItemRecycleViewBinding.inflate(inflater, parent, false)
        return ViewHolder(viewBinding, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.viewBinding.apply {
            textviewArtistName.text = listSong.get(position).songInfo.songArtist
            textviewSongName.text = listSong.get(position).songInfo.songName
        }
        val imgSong =
            if (listSong.get(position).isLocal) ContentUris.withAppendedId(
                Uri.parse(Constant.MEDIA_EXTERNAL_AUDIO_URI),
                listSong.get(position).songInfo.songImg.toLong()
            )
            else listSong.get(position).songInfo.songImg.toUri()
        holder.viewBinding.imgSong.loadByGlide(holder.viewBinding.root.context, imgSong)
    }

    override fun getItemCount(): Int {
        return listSong.size
    }

    inner class ViewHolder(var viewBinding: ItemRecycleViewBinding, listener: ItemClickListener) :
        RecyclerView.ViewHolder(viewBinding.root) {
        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition, listSong)
            }
        }
    }

    interface ItemClickListener {
        fun onItemClick(pos: Int, listSong: MutableList<Song>)
    }
}

package com.example.zingmp3phake.screen.personal

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.databinding.ItemRecycleViewBinding
import com.example.zingmp3phake.utils.Constant
import com.example.zingmp3phake.utils.loadByGlide

class RecyclerViewRecentAdapter(private val listener: ItemClickListener) :
    RecyclerView.Adapter<RecyclerViewRecentAdapter.ViewHolder?>() {

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
        val viewBinding = ItemRecycleViewBinding.inflate(inflater, parent, false)
        return ViewHolder(viewBinding, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = listSong.get(listSong.size - 1 - position)
        holder.viewBinding.apply {
            textviewArtistName.text = song.songInfo.songArtist
            textviewSongName.text = song.songInfo.songName
        }
        val imgSong =
            if (song.isLocal) ContentUris.withAppendedId(
                Uri.parse(Constant.MEDIA_EXTERNAL_AUDIO_URI),
                song.songInfo.songImg.toLong()
            )
            else song.songInfo.songImg.toUri()
        holder.viewBinding.imgSong.loadByGlide(holder.viewBinding.root.context, imgSong)
    }

    override fun getItemCount(): Int {
        return listSong.size
    }

    inner class ViewHolder(var viewBinding: ItemRecycleViewBinding, listener: ItemClickListener) :
        RecyclerView.ViewHolder(viewBinding.root) {
        init {
            itemView.setOnClickListener {
                listener.onItemClick(listSong.size - 1 - adapterPosition, listSong)
            }
        }
    }

    interface ItemClickListener {
        fun onItemClick(pos: Int, listSong: MutableList<Song>)
    }
}

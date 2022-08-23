package com.example.zingmp3phake.screen.search

import android.content.ContentUris
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.zingmp3phake.data.model.Song
import com.example.zingmp3phake.databinding.ItemLoadMoreBinding
import com.example.zingmp3phake.databinding.ItemRecycleViewBinding
import com.example.zingmp3phake.utils.Constant
import com.example.zingmp3phake.utils.loadByGlide

class RecyclerViewSearchAdapter(private val itemClick: ItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val listSong = mutableListOf<Song>()
    private var isLoading = false

    fun setData(songs: List<Song>) {
        listSong.clear()
        listSong.addAll(songs)
        notifyDataSetChanged()
    }

    fun addFooterLoading() {
        isLoading = true
        listSong.add(Song())
    }

    fun addData(songs: List<Song>) {
        isLoading = false
        listSong.removeAt(listSong.size - 1)
        listSong.addAll(songs)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        if (listSong.size > 0 && position == listSong.size - 1 && isLoading) return TYPE_LOAD
        return TYPE_SONG
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_LOAD) {
            val binding =
                ItemLoadMoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return LoadingViewHolder(binding)
        }
        val binding =
            ItemRecycleViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == TYPE_SONG) {
            val holderSong = holder as SongViewHolder
            holderSong.binding.apply {
                textviewSongName.text = listSong.get(position).songInfo.songName
                textviewArtistName.text = listSong.get(position).songInfo.songArtist
            }
            val imgSong =
                if (listSong.get(position).isLocal) ContentUris.withAppendedId(
                    Uri.parse(Constant.MEDIA_EXTERNAL_AUDIO_URI),
                    listSong.get(position).songInfo.songImg.toLong()
                )
                else listSong.get(position).songInfo.songImg.toUri()
            holder.binding.imgSong.loadByGlide(holder.binding.root.context, imgSong)
        }
    }

    override fun getItemCount(): Int {
        return listSong.size
    }

    inner class SongViewHolder(var binding: ItemRecycleViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                itemClick.onItemClick(listSong.get(adapterPosition))
            }
        }
    }

    inner class LoadingViewHolder(binding: ItemLoadMoreBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // TODO no-op
    }

    interface ItemClickListener {
        fun onItemClick(song: Song)
    }

    companion object {
        private const val TYPE_SONG = 1
        private const val TYPE_LOAD = 2
    }
}

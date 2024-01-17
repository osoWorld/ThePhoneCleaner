package com.example.taseenbhaiphonecleanerproject.Adapater

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.taseenbhaiphonecleanerproject.DataClass.ImagesItem
import com.example.taseenbhaiphonecleanerproject.DataClass.VideoItem
import com.example.taseenbhaiphonecleanerproject.R
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VideosAdapterClass(private val videos: MutableList<VideoItem>, val context: Context) :
    RecyclerView.Adapter<VideosAdapterClass.VideoViewHolder>() {

    private val selectedItems = ArrayList<VideoItem>()
    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val view = itemView
        val items: MaterialCardView = itemView.findViewById(R.id.recView_Item)
        val thumbnailImageView: ImageView = itemView.findViewById(R.id.video_ImageView)
        val titleTextView: TextView = itemView.findViewById(R.id.video_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.videos_items, parent, false)
        return VideoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return videos.size
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val videoItem: VideoItem = videos[position]
        holder.titleTextView.text=videoItem.title
        holder.view.setBackgroundColor(if (videoItem.isSelected) Color.CYAN else Color.WHITE)
        Glide.with(context)
            .load(videoItem.uri)
            .skipMemoryCache(false)
            .into(holder.thumbnailImageView)
        holder.items.setOnClickListener {
            videoItem.isSelected = !videoItem.isSelected
            if (videoItem.isSelected) {
                selectedItems.add(videoItem)
                holder.view.background = ContextCompat.getDrawable(context,R.drawable.selected_bg)
            } else {
                selectedItems.remove(videoItem)
                holder.view.background = ContextCompat.getDrawable(context,R.drawable.notselected_bg)
            }
        }
    }
}
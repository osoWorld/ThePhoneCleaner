package com.example.taseenbhaiphonecleanerproject.Adapater

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.graphics.Color
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.taseenbhaiphonecleanerproject.DataClass.AudioDataClass
import com.example.taseenbhaiphonecleanerproject.DataClass.ImagesItem
import com.example.taseenbhaiphonecleanerproject.R
import com.google.android.material.card.MaterialCardView


class AudioAdapterClass(
    val mList: ArrayList<AudioDataClass>,
    val context: Context,

    ) : RecyclerView.Adapter<AudioAdapterClass.AudioViewHolder>() {
    private val selectedItems = ArrayList<AudioDataClass>()
    inner class AudioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

//        val imageView: ImageView = itemView.findViewById(R.id.music_ImageView)
        val textView: TextView = itemView.findViewById(R.id.music_name)
        val cardView: MaterialCardView = itemView.findViewById(R.id.music_recView_Item)
        val sizeimage: TextView = itemView.findViewById(R.id.music_size)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.music_items, parent, false)
        return AudioViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        val items: AudioDataClass = mList[position]
//        Glide.with(context)
//            .load(items.image)
//            .into(holder.imageView)
        holder.textView.text = items.filename
        holder.sizeimage.text=items.size
        holder.cardView.setBackgroundColor(if (items.isSelected) Color.CYAN else Color.WHITE)

        holder.cardView.setOnClickListener {
            items.isSelected=!items.isSelected
            if (items.isSelected){
                selectedItems.add(items)
                holder.itemView.background = ContextCompat.getDrawable(context,R.drawable.selected_bg)

            }else{
                selectedItems.remove(items)
                holder.itemView.background = ContextCompat.getDrawable(context,R.drawable.notselected_bg)
            }

        }

    }


}
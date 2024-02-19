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
import com.example.taseenbhaiphonecleanerproject.DataClass.ImagesItem
import com.example.taseenbhaiphonecleanerproject.R
import com.google.android.material.card.MaterialCardView


class ImagesAdapterClass(
    val mList: ArrayList<ImagesItem>,
    val context: Context,

) : RecyclerView.Adapter<ImagesAdapterClass.ImagesViewHolder>() {
    private val selectedItems = ArrayList<ImagesItem>()
    inner class ImagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageView: ImageView = itemView.findViewById(R.id.video_ImageView)
        val textView: TextView = itemView.findViewById(R.id.video_name)
        val cardView: MaterialCardView = itemView.findViewById(R.id.recView_Item)
        val sizeimage: TextView = itemView.findViewById(R.id.size)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.videos_items, parent, false)
        return ImagesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        val items: ImagesItem = mList[position]
        Glide.with(context)
            .load(items.image)
            .into(holder.imageView)
        holder.textView.text = items.date
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
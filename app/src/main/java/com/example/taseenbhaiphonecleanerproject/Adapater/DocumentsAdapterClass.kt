package com.example.taseenbhaiphonecleanerproject.Adapater

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.taseenbhaiphonecleanerproject.DataClass.DocumentItem
import com.example.taseenbhaiphonecleanerproject.R
import com.google.android.material.card.MaterialCardView

class DocumentsAdapterClass(private val documentList: List<DocumentItem>, val context: Context) :
    RecyclerView.Adapter<DocumentsAdapterClass.ViewHolder>() {
    private val selectedPositions = ArrayList<DocumentItem>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: MaterialCardView = itemView.findViewById(R.id.doc_recView_Item)
        val documentNameTextView: TextView = itemView.findViewById(R.id.doc_name)
        val documentPathTextView: TextView = itemView.findViewById(R.id.date_created)
        val documentSizeTextView: TextView = itemView.findViewById(R.id.doc_size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.doc_items, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val documentInfo = documentList[position]
        holder.cardView.setBackgroundColor(if (documentInfo.isSelected) Color.CYAN else Color.WHITE)
        holder.documentNameTextView.text=documentInfo.name
        holder.documentSizeTextView.text=documentInfo.size.toString()
         holder.cardView.setOnClickListener {
             documentInfo.isSelected=!documentInfo.isSelected
             if (documentInfo.isSelected){
                 selectedPositions.add(documentInfo)
                 holder.itemView.background = ContextCompat.getDrawable(context,R.drawable.selected_bg)

             }else{
                 selectedPositions.remove(documentInfo)
                 holder.itemView.background = ContextCompat.getDrawable(context,R.drawable.notselected_bg)
             }
         }
    }


    override fun getItemCount(): Int {
        return documentList.size
    }


}
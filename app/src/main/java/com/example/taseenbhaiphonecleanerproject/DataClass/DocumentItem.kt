package com.example.taseenbhaiphonecleanerproject.DataClass

import android.net.Uri

data class DocumentItem(
    val name: String,
    val fileuri:Uri?,
    val data: String,
    val size: Long,
    var isSelected: Boolean = false
)

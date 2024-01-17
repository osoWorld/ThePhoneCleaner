package com.example.taseenbhaiphonecleanerproject.DataClass

import android.net.Uri

data class ImagesItem (
    val id: Long?,
    val image: String?,
    val imageuri:Uri?,
    val size: String?,
    val nameimg: String?,
    val date: String?,
    var isSelected: Boolean
)
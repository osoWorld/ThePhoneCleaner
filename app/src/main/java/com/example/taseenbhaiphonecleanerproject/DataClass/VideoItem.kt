package com.example.taseenbhaiphonecleanerproject.DataClass

import android.net.Uri

data class VideoItem(
    val uri: String,
    var videouri:Uri,
    val title: String,
    val duration: Long,
    val size: String,
    var isSelected: Boolean

)

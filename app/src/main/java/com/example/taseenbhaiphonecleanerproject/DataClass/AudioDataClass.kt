package com.example.taseenbhaiphonecleanerproject.DataClass

import android.net.Uri

data class AudioDataClass(
    var audiofilepath: String?,
    var filename: String?,
    val audiofileuri: Uri?,
    var size: String?,
    var isSelected: Boolean,
    var fileid:Long
)

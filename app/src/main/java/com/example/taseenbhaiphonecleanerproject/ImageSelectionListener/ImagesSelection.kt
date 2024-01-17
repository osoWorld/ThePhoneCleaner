package com.example.taseenbhaiphonecleanerproject.ImageSelectionListener

import com.example.taseenbhaiphonecleanerproject.DataClass.ImagesItem

interface ImagesSelection {
    fun GalleryImageSelection(items:ImagesItem)
    fun GalleryImageDeSelection(items:ImagesItem)
}
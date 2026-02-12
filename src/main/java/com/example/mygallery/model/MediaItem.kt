package com.example.mygallery.model

data class MediaItem(
    val path: String,
    val isVideo: Boolean,
    val size: Long,
    val date: Long,
    val album: String
)

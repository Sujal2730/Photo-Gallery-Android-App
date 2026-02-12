package com.example.mygallery.utils

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever

fun getVideoThumbnail(path: String): Bitmap? {
    return try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(path)
        val bitmap = retriever.getFrameAtTime(0)
        retriever.release()
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

package com.example.mygallery.utils

import android.content.Context
import com.example.mygallery.model.MediaItem
import java.io.File

fun loadPrivateMedia(context: Context): List<MediaItem> {

    val privateDir = File(context.filesDir, "private")
    if (!privateDir.exists()) return emptyList()

    return privateDir.listFiles()
        ?.map { file ->
            MediaItem(
                path = file.absolutePath,
                isVideo = file.extension.lowercase() in listOf("mp4", "mkv", "avi"),
                size = file.length(),
                date = file.lastModified(),
                album = "Private"
            )
        }
        ?.sortedByDescending { it.date }
        ?: emptyList()
}

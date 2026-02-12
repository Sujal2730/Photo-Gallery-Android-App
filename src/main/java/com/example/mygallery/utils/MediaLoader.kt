package com.example.mygallery.utils

import android.content.Context
import android.provider.MediaStore
import com.example.mygallery.model.MediaItem
import java.io.File

fun loadAllMedia(context: Context): List<MediaItem> {
    val mediaList = mutableListOf<MediaItem>()

    val uri = MediaStore.Files.getContentUri("external")

    val projection = arrayOf(
        MediaStore.Files.FileColumns.DATA,
        MediaStore.Files.FileColumns.MEDIA_TYPE,
        MediaStore.Files.FileColumns.SIZE,
        MediaStore.Files.FileColumns.DATE_ADDED
    )

    val selection =
        "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE}=?"

    val selectionArgs = arrayOf(
        MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
        MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
    )

    val cursor = context.contentResolver.query(
        uri,
        projection,
        selection,
        selectionArgs,
        "${MediaStore.Files.FileColumns.DATE_ADDED} DESC" // 🔥 DB-level sorting
    )

    cursor?.use {
        val pathIndex = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
        val typeIndex = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)
        val sizeIndex = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
        val dateIndex = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)

        while (it.moveToNext()) {
            val path = it.getString(pathIndex)
            val type = it.getInt(typeIndex)
            val size = it.getLong(sizeIndex)

            // DATE_ADDED is in SECONDS → convert to milliseconds
            val date = it.getLong(dateIndex) * 1000

            mediaList.add(
                MediaItem(
                    path = path,
                    isVideo = type == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO,
                    size = size,
                    date = date,
                    album = File(path).parentFile?.name ?: "Unknown"
                )
            )
        }
    }

    // 🔥 EXTRA SAFETY: ensure newest-first ordering
    return mediaList.sortedByDescending { it.date }
}

package com.example.mygallery.utils

import android.graphics.BitmapFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

data class MediaDetails(
    val name: String,
    val size: String,
    val path: String,
    val resolution: String,
    val date: String
)

fun getMediaDetails(filePath: String): MediaDetails {

    val file = File(filePath)

    // File name
    val name = file.name

    // File size
    val sizeKB = file.length() / 1024
    val size = "$sizeKB KB"

    // Date
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    val date = sdf.format(Date(file.lastModified()))

    // Resolution
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(filePath, options)
    val resolution = "${options.outWidth} x ${options.outHeight}"

    return MediaDetails(
        name = name,
        size = size,
        path = filePath,
        resolution = resolution,
        date = date
    )
}

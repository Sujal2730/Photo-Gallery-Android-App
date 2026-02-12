package com.example.mygallery.utils

import android.media.MediaMetadataRetriever
import java.util.concurrent.TimeUnit

fun getVideoDuration(path: String): String {
    return try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(path)

        val durationMs =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0

        retriever.release()

        val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) % 60

        String.format("%02d:%02d", minutes, seconds)

    } catch (e: Exception) {
        ""
    }
}

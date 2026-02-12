package com.example.mygallery.utils

import android.content.Context
import android.media.MediaScannerConnection
import com.example.mygallery.model.MediaItem
import java.io.File

/* MOVE FILE TO RECYCLE BIN */
fun moveToRecycleBin(context: Context, sourcePath: String): Boolean {
    return try {

        val sourceFile = File(sourcePath)

        val recycleDir = File(context.filesDir, "RecycleBin")
        if (!recycleDir.exists()) recycleDir.mkdirs()

        val destFile = File(recycleDir, sourceFile.name)

        // Move file
        sourceFile.inputStream().use { input ->
            destFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        sourceFile.delete()

        // Force media refresh
        MediaScannerConnection.scanFile(
            context,
            arrayOf(sourcePath),
            null,
            null
        )

        true

    } catch (e: Exception) {
        false
    }
}


/* LOAD RECYCLE BIN MEDIA */
fun loadRecycleBinMedia(context: Context): List<MediaItem> {

    val recycleDir = File(context.filesDir, "RecycleBin")
    if (!recycleDir.exists()) return emptyList()

    return recycleDir.listFiles()?.map { file ->
        MediaItem(
            path = file.absolutePath,
            isVideo = file.extension.lowercase() in listOf("mp4", "mkv", "avi", "3gp"),
            size = file.length(),
            date = file.lastModified(),
            album = "Recycle Bin"
        )
    } ?: emptyList()
}

/* RESTORE FILE FROM RECYCLE BIN */
fun restoreFromRecycleBin(
    context: Context,
    recycleFile: File
): Boolean {
    return try {

        // Restore to Pictures directory so gallery detects it
        val restoreDir =
            context.getExternalFilesDir(null) ?: return false

        val restoredFile = File(restoreDir, recycleFile.name)

        recycleFile.copyTo(restoredFile, overwrite = true)
        val deleted = recycleFile.delete()

        if (deleted) {
            // Refresh MediaStore to show in gallery
            MediaScannerConnection.scanFile(
                context,
                arrayOf(restoredFile.absolutePath),
                null,
                null
            )
        }

        deleted

    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

/* DELETE PERMANENTLY */
fun deletePermanently(recycleFile: File): Boolean {
    return try {
        recycleFile.delete()
    } catch (e: Exception) {
        false
    }
}

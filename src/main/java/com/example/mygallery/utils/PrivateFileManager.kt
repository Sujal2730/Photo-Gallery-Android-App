package com.example.mygallery.utils

import android.content.Context
import android.media.MediaScannerConnection
import java.io.File

fun moveFileToPrivate(context: Context, filePath: String): Boolean {
    return try {

        val sourceFile = File(filePath)

        val privateDir = File(context.filesDir, "PrivateVault")
        if (!privateDir.exists()) privateDir.mkdirs()

        val destFile = File(privateDir, sourceFile.name)

        // Copy file
        sourceFile.inputStream().use { input ->
            destFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        // Delete original
        sourceFile.delete()

        // Refresh MediaStore
        MediaScannerConnection.scanFile(
            context,
            arrayOf(filePath),
            null,
            null
        )

        true

    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

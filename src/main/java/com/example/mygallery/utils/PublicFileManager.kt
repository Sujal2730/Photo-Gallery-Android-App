package com.example.mygallery.utils

import android.content.Context
import android.media.MediaScannerConnection
import android.os.Environment
import java.io.File

fun moveFileToPublic(context: Context, privatePath: String): Boolean {
    return try {
        val privateFile = File(privatePath)
        if (!privateFile.exists()) return false

        val publicDir = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ),
            "MyGallery"
        )

        if (!publicDir.exists()) {
            publicDir.mkdirs()
        }

        val publicFile = File(publicDir, privateFile.name)

        privateFile.copyTo(publicFile, overwrite = true)
        privateFile.delete()

        // 🔔 Notify Android so file appears in gallery
        MediaScannerConnection.scanFile(
            context,
            arrayOf(publicFile.absolutePath),
            null,
            null
        )

        true
    } catch (e: Exception) {
        false
    }
}

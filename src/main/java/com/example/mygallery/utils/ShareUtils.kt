package com.example.mygallery.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

fun shareFiles(context: Context, paths: List<String>) {

    if (paths.isEmpty()) return

    val uris = ArrayList<Uri>()

    paths.forEach { path ->
        val file = File(path)
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
        uris.add(uri)
    }

    val intent = Intent().apply {
        action = Intent.ACTION_SEND_MULTIPLE
        type = "*/*"
        putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(intent, "Share"))
}

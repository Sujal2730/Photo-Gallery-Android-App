package com.example.mygallery.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import com.example.mygallery.utils.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoViewerScreen(
    photos: List<String>,
    startIndex: Int,
    onBack: () -> Unit
) {

    val context = LocalContext.current
    var showDetails by remember { mutableStateOf(false) }

    val pagerState = rememberPagerState(
        initialPage = startIndex,
        pageCount = { photos.size }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Viewer") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("Back")
                    }
                },
                actions = {

                    val currentPath = photos[pagerState.currentPage]

                    IconButton(onClick = {
                        shareFiles(context, listOf(currentPath))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = null)
                    }

                    IconButton(onClick = {
                        moveFileToPrivate(context, currentPath)
                        onBack()
                    }) {
                        Icon(Icons.Default.Lock, contentDescription = null)
                    }

                    IconButton(onClick = {
                        moveToRecycleBin(context, currentPath)
                        onBack()
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                    }

                    IconButton(onClick = {
                        showDetails = true
                    }) {
                        Icon(Icons.Default.Info, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) { page ->

            var scale by remember(page) { mutableStateOf(1f) }
            var offset by remember(page) { mutableStateOf(Offset.Zero) }

            Image(
                painter = rememberAsyncImagePainter(photos[page]),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->

                            // Only zoom when pinch gesture detected
                            if (zoom != 1f) {
                                scale *= zoom
                            }

                            // Only pan when zoomed
                            if (scale > 1f) {
                                offset += pan
                            }
                        }
                    }


                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                if (scale > 1f) {
                                    scale = 1f
                                    offset = Offset.Zero
                                } else {
                                    scale = 2f
                                }
                            }
                        )
                    }
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
            )
        }
    }

    if (showDetails) {

        val details = getMediaDetails(photos[pagerState.currentPage])

        AlertDialog(
            onDismissRequest = { showDetails = false },
            confirmButton = {
                TextButton(onClick = { showDetails = false }) {
                    Text("OK")
                }
            },
            title = { Text("Photo Details") },
            text = {
                Column {
                    Text("Name: ${details.name}")
                    Text("Size: ${details.size}")
                    Text("Resolution: ${details.resolution}")
                    Text("Path: ${details.path}")
                }
            }
        )
    }
}

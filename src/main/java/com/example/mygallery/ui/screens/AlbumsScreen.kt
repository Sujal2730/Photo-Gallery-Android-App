package com.example.mygallery.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.mygallery.model.MediaItem
import com.example.mygallery.utils.*
import androidx.compose.ui.graphics.asImageBitmap
import com.example.mygallery.utils.getVideoThumbnail
import com.example.mygallery.ui.components.FastScroller
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
@OptIn(
    androidx.compose.foundation.ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun AlbumsScreen() {

    val context = LocalContext.current
    val allMedia = loadAllMedia(context)

    val albums = allMedia.groupBy { it.album }

    var selectedAlbum by remember { mutableStateOf<String?>(null) }
    var selectedItems by remember { mutableStateOf(setOf<MediaItem>()) }
    var viewerIndex by remember { mutableStateOf<Int?>(null) }
    var playingVideo by remember { mutableStateOf<String?>(null) }
    var detailsItem by remember { mutableStateOf<MediaItem?>(null) }

    val selectionMode = selectedItems.isNotEmpty()
    val gridState = rememberLazyGridState()

    // ======================
    // ALBUM CONTENT VIEW
    // ======================
    selectedAlbum?.let { albumName ->

        val mediaList = allMedia.filter { it.album == albumName }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(albumName) },
                    navigationIcon = {
                        TextButton(onClick = {
                            selectedAlbum = null
                            selectedItems = emptySet()
                        }) {
                            Text("Back")
                        }
                    },
                    actions = {
                        if (selectionMode) {

                            IconButton(onClick = {
                                selectedItems.forEach {
                                    moveFileToPrivate(context, it.path)
                                }
                                selectedItems = emptySet()
                            }) {
                                Icon(Icons.Default.Lock, null)
                            }

                            if (selectedItems.size == 1) {
                                IconButton(onClick = {
                                    detailsItem = selectedItems.first()
                                }) {
                                    Icon(Icons.Default.Info, null)
                                }
                            }

                            IconButton(onClick = {
                                selectedItems.forEach {
                                    moveToRecycleBin(context, it.path)
                                }
                                selectedItems = emptySet()
                            }) {
                                Icon(Icons.Default.Delete, null)
                            }
                        }
                    }
                )
            }
        ) { padding ->

            Box(modifier = Modifier.padding(padding)) {

                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {

                    items(mediaList) { item ->

                        val isSelected = selectedItems.contains(item)

                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .combinedClickable(
                                    onClick = {
                                        if (selectionMode) {
                                            selectedItems =
                                                if (isSelected) selectedItems - item
                                                else selectedItems + item
                                        } else {
                                            if (item.isVideo) {
                                                playingVideo = item.path
                                            } else {
                                                viewerIndex = mediaList.indexOf(item)
                                            }
                                        }
                                    },
                                    onLongClick = {
                                        selectedItems = selectedItems + item
                                    }
                                )
                        ) {

                            if (item.isVideo) {

                                val thumbnail = remember(item.path) {
                                    getVideoThumbnail(item.path)
                                }

                                thumbnail?.let {
                                    Image(
                                        bitmap = it.asImageBitmap(),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                            } else {

                                Image(
                                    painter = rememberAsyncImagePainter(item.path),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }


                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color(0x66000000))
                                )
                            }
                        }
                    }
                }
                FastScroller(
                    gridState = gridState,
                    getDateForIndex = { index ->
                        mediaList.getOrNull(index)?.date ?: System.currentTimeMillis()
                    }
                )

                // PHOTO VIEWER
                viewerIndex?.let { index ->
                    PhotoViewerScreen(
                        photos = mediaList.map { it.path },
                        startIndex = index,
                        onBack = { viewerIndex = null }
                    )
                }

                // VIDEO PLAYER
                playingVideo?.let { path ->
                    VideoPlayerScreen(
                        videoPath = path,
                        onClose = { playingVideo = null }
                    )
                }

                // DETAILS DIALOG
                detailsItem?.let { item ->

                    val details = getMediaDetails(item.path)

                    AlertDialog(
                        onDismissRequest = { detailsItem = null },
                        title = { Text("Photo Details") },
                        text = {
                            Column {
                                Text("Name: ${details.name}")
                                Text("Size: ${details.size}")
                                Text("Resolution: ${details.resolution}")
                                Text("Path: ${details.path}")
                                Text("Modified: ${details.date}")
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { detailsItem = null }) {
                                Text("OK")
                            }
                        }
                    )
                }
            }
        }

        return
    }

    // ======================
    // ALBUM LIST VIEW
    // ======================

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        val albumList = albums.entries.toList()

        items(albumList.size) { index ->

            val entry = albumList[index]
            val albumName = entry.key
            val cover = entry.value.firstOrNull()?.path
            val count = entry.value.size

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedAlbum = albumName }
            ) {

                cover?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(albumName)
                Text("$count items")
            }
        }
    }
}

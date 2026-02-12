package com.example.mygallery.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.example.mygallery.model.MediaItem
import com.example.mygallery.utils.*
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.Alignment
import com.example.mygallery.utils.getVideoDuration
import com.example.mygallery.ui.components.FastScroller

@OptIn(
    ExperimentalMaterial3Api::class,
    androidx.compose.foundation.ExperimentalFoundationApi::class
)
@Composable
fun HomeScreen() {

    val context = LocalContext.current
    val gridState = rememberLazyGridState()

    var mediaList by remember { mutableStateOf<List<MediaItem>>(emptyList()) }
    var selectedItems by remember { mutableStateOf(setOf<MediaItem>()) }
    var viewerIndex by remember { mutableStateOf<Int?>(null) }
    var playingVideo by remember { mutableStateOf<String?>(null) }
    var detailsItem by remember { mutableStateOf<MediaItem?>(null) }

    val selectionMode = selectedItems.isNotEmpty()

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions.values.all { it }) {
                mediaList = loadAllMedia(context)
            }
        }

    LaunchedEffect(Unit) {
        val granted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED

        if (!granted) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO
                )
            )
        } else {
            mediaList = loadAllMedia(context)
        }
    }

    Scaffold(
        topBar = {
            if (selectionMode) {
                TopAppBar(
                    title = { Text("${selectedItems.size} selected") },
                    actions = {

                        // Share
                        IconButton(onClick = {
                            shareFiles(context, selectedItems.map { it.path })
                            selectedItems = emptySet()
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }


                        // Move to Private
                        IconButton(onClick = {
                            selectedItems.forEach {
                                moveFileToPrivate(context, it.path)
                            }
                            selectedItems = emptySet()
                            mediaList = loadAllMedia(context)
                        }) {
                            Icon(Icons.Default.Lock, contentDescription = "Move to Private")
                        }

                        // Details (only when 1 selected)
                        if (selectedItems.size == 1) {
                            IconButton(onClick = {
                                detailsItem = selectedItems.first()
                            }) {
                                Icon(Icons.Default.Info, contentDescription = "Details")
                            }
                        }

                        // Delete
                        IconButton(onClick = {
                            selectedItems.forEach {
                                moveToRecycleBin(context, it.path)
                            }
                            selectedItems = emptySet()
                            mediaList = loadAllMedia(context)
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                )
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                itemsIndexed(mediaList) { index, item ->

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
                                            viewerIndex = index
                                        }
                                    }
                                },
                                onLongClick = {
                                    selectedItems = selectedItems + item
                                }
                            )

                    ) {

                        // Video thumbnail or image
                        if (item.isVideo) {

                            val thumbnail = remember(item.path) {
                                getVideoThumbnail(item.path)
                            }

                            val duration = remember(item.path) {
                                getVideoDuration(item.path)
                            }

                            thumbnail?.let {
                                Box {

                                    Image(
                                        bitmap = it.asImageBitmap(),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize()
                                    )

                                    // ▶ Play icon overlay
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .size(36.dp),
                                        tint = Color.White
                                    )

                                    // ⏱ Duration bottom-right
                                    Text(
                                        text = duration,
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(4.dp)
                                            .background(Color(0x80000000))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }

                        } else {
                            Image(
                                painter = rememberAsyncImagePainter(item.path),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )
                        }


                        // Selection overlay
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

            // Image viewer
            viewerIndex?.let { index ->
                PhotoViewerScreen(
                    photos = mediaList.map { it.path },
                    startIndex = index,
                    onBack = { viewerIndex = null }
                )
            }

            // Video player
            playingVideo?.let { path ->
                VideoPlayerScreen(
                    videoPath = path,
                    onClose = { playingVideo = null }
                )
            }

            // Details dialog
            detailsItem?.let { item ->
                val details = getMediaDetails(item.path)

                AlertDialog(
                    onDismissRequest = { detailsItem = null },
                    confirmButton = {
                        TextButton(onClick = { detailsItem = null }) {
                            Text("OK")
                        }
                    },
                    title = { Text("Photo Details") },
                    text = {
                        Column {
                            Text("Name: ${details.name}")
                            Text("Size: ${details.size}")
                            Text("Resolution: ${details.resolution}")
                            Text("Date: ${details.date}")
                            Text("Path: ${details.path}")
                        }
                    }
                )
            }
        }
    }
}

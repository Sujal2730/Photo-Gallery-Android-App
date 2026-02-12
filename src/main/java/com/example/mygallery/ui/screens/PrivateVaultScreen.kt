package com.example.mygallery.ui.screens

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
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.mygallery.model.MediaItem
import com.example.mygallery.utils.*
import com.example.mygallery.ui.components.FastScroller

@OptIn(
    androidx.compose.foundation.ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun PrivateVaultScreen() {

    val context = LocalContext.current

    var privateMedia by remember { mutableStateOf(loadPrivateMedia(context)) }
    var selectedItems by remember { mutableStateOf(setOf<MediaItem>()) }
    var viewerIndex by remember { mutableStateOf<Int?>(null) }

    val selectionMode = selectedItems.isNotEmpty()
    val gridState = rememberLazyGridState()

    if (privateMedia.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No private photos or videos")
        }
        return
    }

    Scaffold(
        topBar = {
            if (selectionMode) {
                TopAppBar(
                    title = { Text("${selectedItems.size} selected") },
                    actions = {

                        // Unhide / Move back to gallery
                        IconButton(onClick = {
                            selectedItems.forEach {
                                moveFileToPublic(context, it.path)
                            }
                            selectedItems = emptySet()
                            privateMedia = loadPrivateMedia(context)
                        }) {
                            Icon(Icons.Default.LockOpen, contentDescription = "Unhide")
                        }

                        // Delete permanently
                        IconButton(onClick = {
                            selectedItems.forEach {
                                deletePermanently(java.io.File(it.path))
                            }
                            selectedItems = emptySet()
                            privateMedia = loadPrivateMedia(context)
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
                .fillMaxSize()
                .padding(padding)
        ) {

            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                itemsIndexed(privateMedia) { index, item ->

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
                                        viewerIndex = index   // OPEN VIEWER
                                    }
                                },
                                onLongClick = {
                                    selectedItems = selectedItems + item
                                }
                            )
                    ) {

                        Image(
                            painter = rememberAsyncImagePainter(item.path),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )

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

            // FastScroller overlay
            FastScroller(
                gridState = gridState,
                getDateForIndex = { index ->
                    privateMedia.getOrNull(index)?.date ?: System.currentTimeMillis()
                }
            )
        }
    }

    // IMAGE VIEWER
    viewerIndex?.let { index ->
        PhotoViewerScreen(
            photos = privateMedia.map { it.path },
            startIndex = index,
            onBack = { viewerIndex = null }
        )
    }
}

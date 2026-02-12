package com.example.mygallery.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.mygallery.model.MediaItem
import com.example.mygallery.utils.deletePermanently
import com.example.mygallery.utils.loadRecycleBinMedia
import com.example.mygallery.utils.restoreFromRecycleBin
import com.example.mygallery.ui.components.FastScroller
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import java.io.File

@OptIn(
    androidx.compose.foundation.ExperimentalFoundationApi::class,
    androidx.compose.material3.ExperimentalMaterial3Api::class
)
@Composable
fun RecycleBinScreen() {

    val context = LocalContext.current
    val gridState = rememberLazyGridState()

    var files by remember { mutableStateOf(loadRecycleBinMedia(context)) }
    var selectedItems by remember { mutableStateOf(setOf<MediaItem>()) }
    var viewerIndex by remember { mutableStateOf<Int?>(null) }

    val selectionMode = selectedItems.isNotEmpty()

    LaunchedEffect(Unit) {
        files = loadRecycleBinMedia(context)
    }

    if (files.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Recycle Bin is empty")
        }
        return
    }

    Scaffold(
        topBar = {
            if (selectionMode) {
                TopAppBar(
                    title = { Text("${selectedItems.size} selected") },
                    actions = {

                        // Restore
                        IconButton(onClick = {
                            selectedItems.forEach {
                                restoreFromRecycleBin(context, File(it.path))
                            }
                            selectedItems = emptySet()
                            files = loadRecycleBinMedia(context)
                        }) {
                            Icon(Icons.Default.Restore, contentDescription = "Restore")
                        }

                        // Delete permanently
                        IconButton(onClick = {
                            selectedItems.forEach {
                                deletePermanently(File(it.path))
                            }
                            selectedItems = emptySet()
                            files = loadRecycleBinMedia(context)
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete permanently")
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
                contentPadding = PaddingValues(6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                itemsIndexed(files) { index, file ->

                    val isSelected = selectedItems.contains(file)

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .combinedClickable(
                                onClick = {
                                    if (selectionMode) {
                                        selectedItems =
                                            if (isSelected) selectedItems - file
                                            else selectedItems + file
                                    } else {
                                        viewerIndex = index   // OPEN VIEWER
                                    }
                                },
                                onLongClick = {
                                    selectedItems = selectedItems + file
                                }
                            )
                    ) {

                        Image(
                            painter = rememberAsyncImagePainter(file.path),
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

            // FastScroller
            FastScroller(
                gridState = gridState,
                getDateForIndex = { index ->
                    files.getOrNull(index)?.date ?: System.currentTimeMillis()
                }
            )
        }
    }

    // IMAGE VIEWER
    viewerIndex?.let { index ->
        PhotoViewerScreen(
            photos = files.map { it.path },
            startIndex = index,
            onBack = { viewerIndex = null }
        )
    }
}

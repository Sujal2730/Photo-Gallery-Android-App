package com.example.mygallery.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.mygallery.model.MediaItem
import com.example.mygallery.utils.loadAllMedia

@OptIn(
    ExperimentalMaterial3Api::class,
    androidx.compose.foundation.ExperimentalFoundationApi::class
)
@Composable
fun AlbumDetailScreen(
    albumName: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val mediaList by remember {
        mutableStateOf(
            loadAllMedia(context).filter { it.album == albumName }
        )
    }

    var viewerIndex by remember { mutableStateOf<Int?>(null) }
    var playingVideo by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(albumName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Box(modifier = Modifier.padding(padding)) {

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                itemsIndexed(mediaList) { index, item ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .combinedClickable(
                                onClick = {
                                    if (item.isVideo) {
                                        playingVideo = item.path
                                    } else {
                                        viewerIndex = index
                                    }
                                },
                                onLongClick = {}
                            )
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(item.path),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // 📸 IMAGE VIEWER
            viewerIndex?.let { index ->
                PhotoViewerScreen(
                    photos = mediaList.map { it.path },
                    startIndex = index,
                    onBack = { viewerIndex = null }
                )
            }

            // 🎬 VIDEO PLAYER
            playingVideo?.let { path ->
                VideoPlayerScreen(
                    videoPath = path,
                    onClose = { playingVideo = null }
                )
            }
        }
    }
}

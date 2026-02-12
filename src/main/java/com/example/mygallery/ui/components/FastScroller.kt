package com.example.mygallery.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FastScroller(
    gridState: LazyGridState,
    getDateForIndex: (Int) -> Long
) {

    val coroutineScope = rememberCoroutineScope()

    var isDragging by remember { mutableStateOf(false) }
    var thumbOffset by remember { mutableStateOf(0f) }

    val totalItems = gridState.layoutInfo.totalItemsCount
    val visibleItems = gridState.layoutInfo.visibleItemsInfo.size

    if (totalItems == 0) return

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.CenterEnd
    ) {

        Box(
            modifier = Modifier
                .width(24.dp)
                .fillMaxHeight()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->

                        isDragging = true

                        thumbOffset += dragAmount.y

                        val proportion =
                            (thumbOffset / size.height).coerceIn(0f, 1f)

                        val targetIndex =
                            (proportion * totalItems).toInt()

                        coroutineScope.launch {
                            gridState.scrollToItem(
                                targetIndex.coerceIn(0, totalItems - 1)
                            )
                        }
                    }
                }
        ) {

            // Scroll thumb
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = thumbOffset.dp)
                    .width(8.dp)
                    .height(60.dp)
                    .background(Color.Gray)
            )

            // Floating date
            if (isDragging) {

                val currentIndex =
                    gridState.firstVisibleItemIndex

                val dateMillis =
                    getDateForIndex(currentIndex)

                val formatter =
                    SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(end = 32.dp)
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(8.dp)
                ) {
                    Text(
                        text = formatter.format(Date(dateMillis)),
                        color = Color.White
                    )
                }
            }
        }
    }
}

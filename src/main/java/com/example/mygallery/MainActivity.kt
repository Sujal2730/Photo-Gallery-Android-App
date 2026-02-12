package com.example.mygallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.mygallery.ui.screens.AlbumsScreen
import com.example.mygallery.ui.screens.HomeScreen
import com.example.mygallery.ui.screens.PrivateScreen
import com.example.mygallery.ui.theme.MyGalleryTheme
import com.example.mygallery.utils.AppLockManager
import com.example.mygallery.ui.screens.RecycleBinScreen
import androidx.compose.material.icons.filled.Delete

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyGalleryTheme {
                MyGalleryApp()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        AppLockManager.lock()
    }
}

@Composable
fun MyGalleryApp() {

    var currentDestination by rememberSaveable {
        mutableStateOf(AppDestinations.HOME)
    }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach { destination ->
                item(
                    icon = {
                        Icon(
                            destination.icon,
                            contentDescription = destination.label
                        )
                    },
                    label = { Text(destination.label) },
                    selected = destination == currentDestination,
                    onClick = { currentDestination = destination }
                )
            }
        }
    ) {

        when (currentDestination) {
            AppDestinations.HOME -> HomeScreen()
            AppDestinations.ALBUMS -> AlbumsScreen()
            AppDestinations.PRIVATE -> PrivateScreen()
            AppDestinations.RECYCLE -> RecycleBinScreen()   // NEW
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("All", Icons.Default.Home),
    ALBUMS("Albums", Icons.Default.Favorite),
    PRIVATE("Private", Icons.Default.Lock),
    RECYCLE("Bin", Icons.Default.Delete)   // NEW
}


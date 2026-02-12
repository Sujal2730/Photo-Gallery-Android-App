package com.example.mygallery.ui.screens

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.mygallery.utils.AppLockManager
import com.example.mygallery.utils.SecurityManager

@Composable
fun PrivateScreen() {

    val context = LocalContext.current

    // Check if PIN exists
    val pinExists = SecurityManager.getPin(context) != null

    // Local recomposition trigger when unlock state changes
    var refresh by remember { mutableStateOf(false) }

    when {

        // First time user → Set PIN
        !pinExists -> {
            SetPinScreen {
                AppLockManager.unlock()
                refresh = !refresh
            }
        }

        // Locked → Ask for PIN
        !AppLockManager.isUnlocked -> {
            EnterPinScreen {
                AppLockManager.unlock()
                refresh = !refresh
            }
        }

        // Unlocked → Show Private Vault
        else -> {
            PrivateVaultScreen()
        }
    }
}

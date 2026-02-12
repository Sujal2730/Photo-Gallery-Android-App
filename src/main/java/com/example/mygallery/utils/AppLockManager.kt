package com.example.mygallery.utils

import androidx.compose.runtime.mutableStateOf

object AppLockManager {

    private val unlockedState = mutableStateOf(false)

    val isUnlocked: Boolean
        get() = unlockedState.value

    fun unlock() {
        unlockedState.value = true
    }

    fun lock() {
        unlockedState.value = false
    }
}

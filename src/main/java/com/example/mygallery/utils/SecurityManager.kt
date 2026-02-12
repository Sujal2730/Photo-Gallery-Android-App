package com.example.mygallery.utils

import android.content.Context

object SecurityManager {

    private const val PREF_NAME = "private_vault"
    private const val KEY_PIN = "pin"

    fun savePin(context: Context, pin: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_PIN, pin).apply()
    }

    fun getPin(context: Context): String? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_PIN, null)
    }

    fun verifyPin(context: Context, enteredPin: String): Boolean {
        return getPin(context) == enteredPin
    }
}

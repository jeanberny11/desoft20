package com.dreamsoft.desoft20.utils.extentions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.core.content.edit
import java.util.UUID

object UtilsHelper {

    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context): String {
        // Try to get a stable identifier
        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        // Fallback to stored UUID if Android ID is not available
        if (androidId.isNullOrEmpty() || androidId == "9774d56d682e549c") {
            // "9774d56d682e549c" is known to occur on some emulators
            return getStoredUUID(context)
        }
        return androidId
    }

    private fun getStoredUUID(context: Context): String {
        val prefs = context.getSharedPreferences("device_prefs", Context.MODE_PRIVATE)
        var uuid = prefs.getString("DEVICE_UUID", null)

        if (uuid == null) {
            uuid = UUID.randomUUID().toString()
            prefs.edit { putString("DEVICE_UUID", uuid) }
        }

        return uuid
    }

    fun hideKeyboard(context: Activity) {
        val view = context.currentFocus
        if (view != null) {
            (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                view.windowToken,
                0
            )
        }
    }
}
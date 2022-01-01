package com.udacity.project4.common.extensions

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.fragment.app.Fragment
import com.udacity.project4.BuildConfig

fun Fragment.openAppSettings() {
    startActivity(Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    })
}

fun Fragment.openDeviceLocationsSettings() {
    startActivity(Intent().apply {
        action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    })
}
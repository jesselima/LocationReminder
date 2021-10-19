package com.udacity.locationreminder.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

fun Context.isPermissionNotGranted(permission: String): Boolean {
    return ActivityCompat.checkSelfPermission(
        this,
        permission
    ) != PackageManager.PERMISSION_GRANTED
}

fun Activity.requestPermissions(permissions: Array<String>, requestCode: Int) {
    ActivityCompat.requestPermissions(this, permissions, requestCode)
}
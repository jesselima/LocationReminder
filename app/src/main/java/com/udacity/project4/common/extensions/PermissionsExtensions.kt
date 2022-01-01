package com.udacity.project4.common.extensions

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

fun Fragment.isPermissionNotGranted(permission: String): Boolean {
    return  PackageManager.PERMISSION_GRANTED !=
            ActivityCompat.checkSelfPermission(requireContext(), permission)
}

fun Fragment.isPermissionGranted(permission: String): Boolean {
    return  PackageManager.PERMISSION_GRANTED ==
            ActivityCompat.checkSelfPermission(requireContext(), permission)
}

fun Fragment.hasRequiredLocationPermissions(): Boolean {
    return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        isPermissionGranted(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    } else {
        (isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION) ||
                isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION))
    }
}

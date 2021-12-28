package com.udacity.project4.common.extensions

import android.content.pm.PackageManager
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
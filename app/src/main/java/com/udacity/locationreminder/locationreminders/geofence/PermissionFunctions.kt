package com.udacity.locationreminder.locationreminders.geofence

import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

fun Fragment.isPermissionNotGranted(permission: String): Boolean {
    return  PackageManager.PERMISSION_GRANTED !=
                ActivityCompat.checkSelfPermission(requireContext(), permission)
}

fun isResultDenied(grantedResult: Int) : Boolean {
    return grantedResult == PackageManager.PERMISSION_DENIED
}

fun isResultGranted(grantedResult: Int) : Boolean {
    return grantedResult == PackageManager.PERMISSION_GRANTED
}

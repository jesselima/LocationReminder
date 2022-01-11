package com.udacity.project4.common.extensions

import android.Manifest.permission.*
import android.annotation.TargetApi
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest

fun Fragment.isPermissionGranted(permission: String): Boolean {
    return  PackageManager.PERMISSION_GRANTED ==
            ActivityCompat.checkSelfPermission(requireContext(), permission)
}

@TargetApi(29)
fun Fragment.hasRequiredLocationPermissions(): Boolean {
    return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        isPermissionGranted(ACCESS_BACKGROUND_LOCATION)
    } else {
        (isPermissionGranted(ACCESS_COARSE_LOCATION) ||
                isPermissionGranted(ACCESS_FINE_LOCATION))
    }
}

fun Fragment.checkDeviceLocationSettings(
    resolve: Boolean = true,
    requestCode: Int,
    onLocationSettingsResult: ((Boolean) -> Unit)? = null,
) {
    val locationRequest = LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_LOW_POWER
    }

    val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
    val settingsClient = LocationServices.getSettingsClient(requireActivity())
    val locationSettingsResponseTask = settingsClient.checkLocationSettings(builder.build())

    locationSettingsResponseTask.addOnCompleteListener {
        if (it.isSuccessful) onLocationSettingsResult?.invoke(true)
    }

    locationSettingsResponseTask.addOnFailureListener { exception ->
        if (exception is ResolvableApiException && resolve){
            try {
                exception.startResolutionForResult(requireActivity(), requestCode)
            } catch (sendEx: IntentSender.SendIntentException) {
                Log.d(
                    this::class.java.simpleName,
                    "Error getting location settings resolution: " + sendEx.message)
            }
        } else {
            onLocationSettingsResult?.invoke(false)
        }
    }
}

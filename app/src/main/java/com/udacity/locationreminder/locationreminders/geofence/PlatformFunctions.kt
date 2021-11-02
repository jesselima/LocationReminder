package com.udacity.locationreminder.locationreminders.geofence

import android.os.Build

/**
 * osVersion = Build.VERSION_CODES.M
 */
fun isAndroidOsEqualsOrGreaterThan(osVersion: Int): Boolean {
    return Build.VERSION.SDK_INT >= osVersion
}
package com.udacity.project4.common.extensions

import android.os.Build

/**
 * osVersion = Build.VERSION_CODES.M
 */
fun isAndroidOsEqualsOrGreaterThan(osVersion: Int): Boolean {
    return Build.VERSION.SDK_INT >= osVersion
}
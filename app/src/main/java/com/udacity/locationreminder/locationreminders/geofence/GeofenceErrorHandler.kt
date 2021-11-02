package com.udacity.locationreminder.locationreminders.geofence

import android.content.Context
import com.google.android.gms.location.GeofenceStatusCodes
import com.udacity.locationreminder.R

/**
 * Returns the error string for a geofencing error code.
 */
fun handleGeofenceError(context: Context, errorCode: Int): String {
    return when (errorCode) {
        GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> context.resources.getString(
            R.string.geofence_not_available
        )
        GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> context.resources.getString(
            R.string.geofence_too_many_geofences
        )
        GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> context.resources.getString(
            R.string.geofence_too_many_pending_intents
        )
        else -> context.resources.getString(R.string.unknown_geofence_error)
    }
}

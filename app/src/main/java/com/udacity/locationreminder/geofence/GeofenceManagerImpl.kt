package com.udacity.locationreminder.geofence

import android.Manifest
import android.app.PendingIntent
import androidx.annotation.RequiresPermission
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.udacity.locationreminder.R

class GeofenceManagerImpl: GeofenceManager {

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    override fun addGeofence(
        geofenceClient: GeofencingClient,
        geofencePendingIntent: PendingIntent,
        id: String,
        latitude: Double?,
        longitude: Double?,
        circularRadius: Float,
        expiration: Long,
        transitionType: Int,
        onAddGeofenceSuccess: (() -> Unit)?,
        onAddGeofenceFailure: ((Int) -> Unit)?
    ) {
        val geofence = Geofence.Builder()
            .setRequestId(id)
            .setCircularRegion(latitude ?: 0.0, longitude ?: 0.0, circularRadius)
            .setExpirationDuration(expiration)
            .setTransitionTypes(transitionType)
            .build()

        geofenceClient.addGeofences(createGeofenceRequest(geofence), geofencePendingIntent).run {
            addOnSuccessListener {
                onAddGeofenceSuccess?.invoke()
            }
            addOnFailureListener {
                onAddGeofenceFailure?.invoke((it as ApiException).mapCodeToMessage())
            }
        }
    }

    override fun removeGeofence(
        geofenceClient: GeofencingClient,
        id: String,
        onRemoveGeofenceSuccess: (() -> Unit)?,
        onRemoveGeofenceFailure: ((Int) -> Unit)?
    ) {
        geofenceClient.removeGeofences(listOf(id)).run {
            addOnSuccessListener {
                onRemoveGeofenceSuccess?.invoke()
            }
            addOnFailureListener {
                onRemoveGeofenceFailure?.invoke((it as ApiException).mapCodeToMessage())
            }
        }
    }

    private fun createGeofenceRequest(geofence: Geofence) = GeofencingRequest.Builder()
        .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL)
        .addGeofence(geofence)
        .build()

    private fun ApiException.mapCodeToMessage(): Int {
        return when (statusCode) {
            GeofenceStatusCodes.GEOFENCE_INSUFFICIENT_LOCATION_PERMISSION -> {
                R.string.geofence_error_background_permission_required
            }
            GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> {
               R.string.geofence_error_geofence_not_available
            }
            GeofenceStatusCodes.GEOFENCE_REQUEST_TOO_FREQUENT -> {
                R.string.geofence_error_too_many_geofence_request
            }
            GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES,
            GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> {
                R.string.geofence_error_too_many_geofences
            }
            else -> R.string.geofence_error_something_went_wrong
        }
    }
}
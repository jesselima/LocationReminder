package com.udacity.project4.geofence

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresPermission
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.udacity.project4.R
import com.udacity.project4.features.reminderslist.PENDING_INTENT_REQUEST_CODE

private const val SINGLE_GEOFENCE_REMOVAL = 1

class GeofenceManagerImpl(context: Context): GeofenceManager {

    private val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
    } else {
        PendingIntent.FLAG_UPDATE_CURRENT
    }

    private val geofencePendingIntent = PendingIntent.getBroadcast(
        context,
        PENDING_INTENT_REQUEST_CODE,
        Intent(context, GeofenceBroadcastReceiver::class.java),
        flags
    )

    /**
     * This method must be called only when permission ACCESS_FINE_LOCATION or ACCESS_COARSE_LOCATION
     * have been allowed by the user.
     */
    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    override fun addGeofence(
        geofenceClient: GeofencingClient,
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

    override fun removeGeofences(
        geofenceClient: GeofencingClient,
        remindersIds: List<String>,
        onRemoveGeofenceSuccess: ((Boolean) -> Unit)?,
        onRemoveGeofenceFailure: ((Int) -> Unit)?
    ) {
        geofenceClient.removeGeofences(remindersIds).run {
            addOnSuccessListener {
                onRemoveGeofenceSuccess?.invoke(remindersIds.size > SINGLE_GEOFENCE_REMOVAL)
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
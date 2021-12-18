package com.udacity.locationreminder.geofence

import android.app.PendingIntent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import java.util.concurrent.TimeUnit

interface GeofenceManager {

    fun addGeofence(
        geofenceClient: GeofencingClient,
        geofencePendingIntent: PendingIntent,
        id: String,
        latitude: Double? = 0.0,
        longitude: Double? = 0.0,
        circularRadius: Float = 100f,
        expiration: Long = TimeUnit.DAYS.toMillis(7),
        transitionType: Int = Geofence.GEOFENCE_TRANSITION_ENTER,
        onAddGeofenceSuccess: (() -> Unit)? = null,
        onAddGeofenceFailure: ((Int) -> Unit)? = null
    )

    fun removeGeofence(
        geofenceClient: GeofencingClient,
        id: String,
        onRemoveGeofenceSuccess: (() -> Unit)? = null,
        onRemoveGeofenceFailure: ((Int) -> Unit)? = null
    )
}

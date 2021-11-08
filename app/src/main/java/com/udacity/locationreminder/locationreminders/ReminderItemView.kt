package com.udacity.locationreminder.locationreminders

import com.google.android.gms.location.Geofence
import com.udacity.locationreminder.locationreminders.data.dto.ReminderData
import java.io.Serializable
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * data class acts as a data mapper between the DB and the UI
 */
data class ReminderItemView(
    var locationName: String? = null,
    var title: String? = null,
    var description: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var isPoi: Boolean? = null,
    var poiId: String? = null,
    var circularRadius: Float = 50f,
    var expiration: Long = TimeUnit.DAYS.toMillis(1),
    var transitionType: Int = Geofence.GEOFENCE_TRANSITION_ENTER,
    var id: Long = System.currentTimeMillis(),
    var isGeofenceEnable: Boolean = false
) : Serializable

fun ReminderItemView.mapToDataModel() : ReminderData {
    return ReminderData(
        id = id,
        locationName = locationName,
        title = title,
        description = description,
        latitude = latitude,
        longitude = longitude,
        isPoi = isPoi,
        poiId = poiId,
        circularRadius = circularRadius,
        expiration = expiration,
        transitionType = transitionType,
        isGeofenceEnable = isGeofenceEnable
    )
}

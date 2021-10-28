package com.udacity.locationreminder.locationreminders

import com.google.android.gms.maps.model.LatLng
import com.udacity.locationreminder.locationreminders.data.dto.ReminderData
import java.io.Serializable
import java.util.*

/**
 * data class acts as a data mapper between the DB and the UI
 */
data class ReminderItemView(
    var title: String? = null,
    var description: String? = null,
    var isPoi: Boolean? = null,
    var poiId: String? = null,
    var locationName: String? = null,
    var latLng: LatLng? = null,
    val id: String = UUID.randomUUID().toString()
) : Serializable

fun ReminderItemView.mapToDataModel() : ReminderData {
    return ReminderData(
        title = title,
        description = description,
        locationName = locationName,
        latitude = latLng?.latitude,
        longitude = latLng?.longitude,
        isPoi = isPoi,
        poiId = poiId
    )
}
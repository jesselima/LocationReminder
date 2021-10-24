package com.udacity.locationreminder.locationreminders.reminderslist

import com.udacity.locationreminder.locationreminders.data.dto.ReminderData
import java.io.Serializable
import java.util.*

/**
 * data class acts as a data mapper between the DB and the UI
 */
data class ReminderItemView(
    var title: String?,
    var description: String?,
    var isPoi: Boolean?,
    var poiId: String?,
    var location: String?,
    var latitude: Double?,
    var longitude: Double?,
    val id: String = UUID.randomUUID().toString()
) : Serializable

fun ReminderItemView.mapToDataModel() : ReminderData {
    return ReminderData(
        title = title,
        description = description,
        location = location,
        latitude = latitude,
        longitude = longitude,
        isPoi = isPoi,
        poiId = poiId
    )
}
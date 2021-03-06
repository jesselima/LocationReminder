package com.udacity.project4.shareddata.localdatasource.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udacity.project4.sharedpresentation.ReminderItemView

/**
 * Immutable model class for a Reminder. In order to compile with Room
 *
 * @param title title of the reminder
 * @param description description of the reminder
 * @param locationName location name of the reminder
 * @param latitude latitude of the reminder location
 * @param longitude longitude of the reminder location
 * @param id id of the reminder
 */

@Entity(tableName = "reminders")
data class ReminderData(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "location") val locationName: String?,
    @ColumnInfo(name = "latitude") val latitude: Double?,
    @ColumnInfo(name = "longitude") val longitude: Double?,
    @ColumnInfo(name = "is_poi") val isPoi: Boolean?,
    @ColumnInfo(name = "poi_id") val poiId: String?,
    @ColumnInfo(name = "circularRadius") val circularRadius: Float?,
    @ColumnInfo(name = "expiration") val expiration: Long?,
    @ColumnInfo(name = "transitionType") val transitionType: Int?,
    @ColumnInfo(name = "is_geofence_enable") val isGeofenceEnable: Boolean?,
)

fun ReminderData.mapToPresentationModel() : ReminderItemView {
    return ReminderItemView(
        id = id,
        title = title,
        description = description,
        locationName = locationName,
        latitude = latitude ?: 0.0,
        longitude = longitude ?: 0.0,
        isPoi = isPoi,
        poiId = poiId,
        circularRadius = circularRadius ?: 50f,
        expiration = expiration ?: -1,
        transitionType = transitionType ?: 1,
        isGeofenceEnable = isGeofenceEnable ?: false
    )
}

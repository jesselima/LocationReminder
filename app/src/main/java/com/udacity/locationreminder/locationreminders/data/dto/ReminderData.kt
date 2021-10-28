package com.udacity.locationreminder.locationreminders.data.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import com.udacity.locationreminder.locationreminders.ReminderItemView
import java.util.*

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
    @ColumnInfo(name = "title") var title: String?,
    @ColumnInfo(name = "description") var description: String?,
    @ColumnInfo(name = "location") var locationName: String?,
    @ColumnInfo(name = "latitude") var latitude: Double?,
    @ColumnInfo(name = "longitude") var longitude: Double?,
    @ColumnInfo(name = "is_poi") var isPoi: Boolean?,
    @ColumnInfo(name = "poi_id") var poiId: String?,
    @PrimaryKey @ColumnInfo(name = "entry_id") val id: String = UUID.randomUUID().toString()
)

fun ReminderData.mapToPresentationModel() : ReminderItemView {
    return ReminderItemView(
        title = title,
        description = description,
        locationName = locationName,
        latLng = LatLng(latitude ?: 0.0, longitude ?: 0.0),
        isPoi = isPoi,
        poiId = poiId
    )
}

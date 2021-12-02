package com.udacity.locationreminder.stubs

import com.google.android.gms.location.Geofence
import com.udacity.locationreminder.shareddata.localdatasource.models.ReminderData
import com.udacity.locationreminder.sharedpresentation.ReminderItemView

val reminderItemView =  ReminderItemView(
    id = 2,
    locationName = "BlaBla Market",
    title = "Barbecue stuff",
    description = "Meat, drinks, tomato",
    latitude = -23.69630837127584,
    longitude = -46.66913866996765,
    isPoi = true,
    poiId = "ChIJAAAAAAAAAAARcs7Roitt0Yk",
    circularRadius = 50.0f,
    expiration = 86400000,
    transitionType = 1,
    isGeofenceEnable = true
)

val reminderData = ReminderData(
    id = System.currentTimeMillis(),
    title= "Grow tomatoes",
    description ="Do not forget the fertilizer",
    locationName = "Mars, South Pole",
    latitude = -90.0,
    longitude = -210.0,
    isPoi = true,
    poiId = "etAlienHumansTomato",
    circularRadius = 450.0f,
    expiration = -1,
    transitionType = Geofence.GEOFENCE_TRANSITION_EXIT,
    isGeofenceEnable = true,
)
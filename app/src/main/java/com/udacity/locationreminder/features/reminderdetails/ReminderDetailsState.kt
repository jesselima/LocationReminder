package com.udacity.locationreminder.features.reminderdetails

import com.udacity.locationreminder.sharedpresentation.ReminderItemView

data class ReminderDetailsState(
    val isLoading: Boolean = false,
    val reminderItemView: ReminderItemView? = null,
)

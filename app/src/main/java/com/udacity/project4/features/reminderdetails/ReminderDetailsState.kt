package com.udacity.project4.features.reminderdetails

import com.udacity.project4.sharedpresentation.ReminderItemView

data class ReminderDetailsState(
    val isLoading: Boolean = false,
    val reminderItemView: ReminderItemView? = null,
)

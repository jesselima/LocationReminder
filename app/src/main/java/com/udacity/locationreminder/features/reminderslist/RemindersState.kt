package com.udacity.locationreminder.features.reminderslist

import com.udacity.locationreminder.sharedpresentation.ReminderItemView

data class RemindersState(
    val isLoading: Boolean = false,
    var reminders: List<ReminderItemView> = emptyList()
)

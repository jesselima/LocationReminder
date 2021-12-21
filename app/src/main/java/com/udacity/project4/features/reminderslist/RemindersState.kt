package com.udacity.project4.features.reminderslist

import com.udacity.project4.sharedpresentation.ReminderItemView

data class RemindersState(
    val isLoading: Boolean = false,
    var reminders: List<ReminderItemView> = emptyList()
)

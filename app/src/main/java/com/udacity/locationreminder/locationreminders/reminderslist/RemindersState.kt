package com.udacity.locationreminder.locationreminders.reminderslist

import com.udacity.locationreminder.locationreminders.ReminderItemView

data class RemindersState(
    val isLoading: Boolean = false,
    var reminders: List<ReminderItemView> = emptyList()
)

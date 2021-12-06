package com.udacity.locationreminder.features.addreminder.presentation

import com.udacity.locationreminder.sharedpresentation.ReminderItemView

data class AddReminderState(
    val isLoading: Boolean = false,
    val selectedReminder: ReminderItemView? = null,
)
